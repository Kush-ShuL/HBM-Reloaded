package com.hbm.radiation;

import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.bomb.nuclear.NuclearChunkAccess;
import com.hbm.bomb.nuclear.NuclearTerrain;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

/** Simple's surface rules, with persistent complete coverage instead of random chunk/column lotteries. */
final class RadiationTerrainEffects implements RadiationEffects {
    private static final long TICK_BUDGET_NANOS = 2_000_000L;
    private static final int WINDOW_SIZE = 4, MAX_SURFACE_STEPS = 4096, COLD_ROTATION_TICKS = 20;
    private final Map<ServerLevel, WorldWork> worlds = new IdentityHashMap<>();

    private static final class Slot {
        // At most four nine-chunk halos: cold queues cannot evict one another from a shared LRU.
        final NuclearChunkAccess chunks = new NuclearChunkAccess();
        LevelChunk ready;
        int coldTicks;
    }

    private static final class WorldWork {
        final Map<Long, Slot> slots = new HashMap<>();
        int next;
        void close() { slots.values().forEach(slot -> slot.chunks.close()); slots.clear(); }
    }

    private enum SurfaceStep { COMPLETE_COLUMN, CONTINUE_COLUMN, RETRY }

    @Override
    public void onWorldEffects(ServerLevel level, Map<Long, Float> radiation, RadiationSettings settings) {
        var saved = RadiationSavedData.get(level);
        for (var entry : radiation.entrySet())
            saved.enqueueTerrain(entry.getKey(), entry.getValue(), settings.worldEffects());
        tick(level, saved, settings);
    }

    void onWorldEffects(ServerLevel level, RadiationGrid radiation, RadiationSettings settings) {
        // The owned grid records eligibility at load/set/final diffusion writes, not via a full-map tick scan.
        tick(level, RadiationSavedData.get(level), settings);
    }

    private void tick(ServerLevel level, RadiationSavedData saved, RadiationSettings settings) {
        var config = settings.worldEffects();
        if (!config.enabled() || config.sampledChunks() == 0 || config.attemptsPerChunk() == 0 || !saved.hasTerrainWork()) {
            unload(level);
            return;
        }
        long deadline = System.nanoTime() + TICK_BUDGET_NANOS;
        var window = saved.terrainWindow(WINDOW_SIZE);
        var world = worlds.computeIfAbsent(level, unused -> new WorldWork());
        world.slots.entrySet().removeIf(entry -> {
            for (var work : window) if (work.chunk == entry.getKey()) return false;
            entry.getValue().chunks.close();
            return true;
        });
        // Preserve zero/positive throughput controls without multiplying the old redundant scans.
        int steps = (int) Math.min(MAX_SURFACE_STEPS, (long) config.sampledChunks() * config.attemptsPerChunk() * 256L);
        int prepared = 0, ready = 0, finished = 0, all = (1 << window.size()) - 1;
        var pos = new BlockPos.MutableBlockPos();
        while (steps > 0 && System.nanoTime() < deadline) {
            int index = Math.floorMod(world.next++, window.size());
            int bit = 1 << index;
            if ((finished & bit) != 0) {
                if (finished == all) break;
                continue;
            }
            var work = window.get(index);
            var slot = world.slots.computeIfAbsent(work.chunk, unused -> new Slot());
            if ((prepared & bit) == 0) {
                prepared |= bit;
                slot.ready = slot.chunks.getWithNeighbors(level, RadiationGrid.chunkX(work.chunk), RadiationGrid.chunkZ(work.chunk));
                if (slot.ready != null) {
                    slot.coldTicks = 0;
                    ready |= bit;
                } else if (++slot.coldTicks >= COLD_ROTATION_TICKS && saved.terrainSize() > window.size()) {
                    // No cursor/dose is discarded. Allow jobs beyond a slow halo to get their turn.
                    saved.deferTerrain(work);
                    slot.chunks.close();
                    world.slots.remove(work.chunk);
                    finished |= bit;
                }
            }
            if ((ready & bit) != 0) {
                // A future/halo preparation may have consumed this tick's allowance.
                if (System.nanoTime() >= deadline) break;
                SurfaceStep result = surfaceStep(level, slot.ready, work, pos);
                steps--;
                if (result == SurfaceStep.COMPLETE_COLUMN) saved.advanceTerrain(work);
                if (result == SurfaceStep.RETRY || work.column == 256) {
                    if (result == SurfaceStep.RETRY) saved.deferTerrain(work);
                    slot.chunks.close();
                    world.slots.remove(work.chunk);
                    finished |= bit;
                }
            }
            if (finished == all || (prepared == all && (ready & ~finished) == 0)) break;
        }
        if (!saved.hasTerrainWork()) unload(level);
    }

    private static SurfaceStep surfaceStep(ServerLevel level, LevelChunk chunk, RadiationSavedData.TerrainWork work,
                                          BlockPos.MutableBlockPos pos) {
        int x = work.column >>> 4, z = work.column & 15;
        int worldX = (RadiationGrid.chunkX(work.chunk) << 4) + x;
        int worldZ = (RadiationGrid.chunkZ(work.chunk) << 4) + z;
        // Old Chunk height used positive light opacity, not modern WORLD_SURFACE's non-air test.
        // Dead leaves/layers/glass remain in place while exposing the next actual source surface.
        int y = chunk.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
        pos.set(worldX, y, worldZ);
        while (y >= level.getMinBuildHeight() && chunk.getBlockState(pos).getLightBlock(level, pos) == 0)
            pos.set(worldX, --y, worldZ);
        if (y < level.getMinBuildHeight()) return SurfaceStep.COMPLETE_COLUMN;
        if (y + 1 < level.getMaxBuildHeight()) {
            // Source oldHeight-rand(2) could hit a zero-opacity plant one above the opacity surface.
            pos.set(worldX, y + 1, worldZ);
            var above = chunk.getBlockState(pos);
            if (above.is(Blocks.GRASS_BLOCK) || above.is(Blocks.SHORT_GRASS) || above.is(Blocks.FERN) || liveLeaves(above))
                return convertSurface(level, above, pos);
        }
        pos.set(worldX, y, worldZ);
        var state = chunk.getBlockState(pos);
        return convertSurface(level, state, pos);
    }

    private static boolean liveLeaves(net.minecraft.world.level.block.state.BlockState state) {
        return !state.is(NuclearTerrain.LEAVES.get()) && LegacyMaterials.of(state).name().equals("leaves");
    }

    private static SurfaceStep convertSurface(ServerLevel level, net.minecraft.world.level.block.state.BlockState state,
                                              BlockPos.MutableBlockPos pos) {
        if (state.is(Blocks.GRASS_BLOCK)) {
            return level.setBlock(pos, NuclearTerrain.EARTH.get().defaultBlockState(), 3)
                    ? SurfaceStep.COMPLETE_COLUMN : SurfaceStep.RETRY;
        }
        if (state.is(Blocks.SHORT_GRASS) || state.is(Blocks.FERN)) {
            return level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3)
                    ? SurfaceStep.CONTINUE_COLUMN : SurfaceStep.RETRY;
        }
        if (liveLeaves(state)) {
            // Keep the source leaf-result draw, including its six dead-leaf outcomes out of seven.
            boolean deadLeaves = level.random.nextInt(7) <= 5;
            return level.setBlock(pos, deadLeaves ? NuclearTerrain.LEAVES.get().defaultBlockState() : Blocks.AIR.defaultBlockState(), 3)
                    ? SurfaceStep.CONTINUE_COLUMN : SurfaceStep.RETRY;
        }
        // An unsupported positive-opacity surface (e.g. a solid roof or water) still blocks the column.
        return SurfaceStep.COMPLETE_COLUMN;
    }

    void unload(ServerLevel level) {
        var work = worlds.remove(level);
        if (work != null) work.close();
    }

    void close() { worlds.values().forEach(WorldWork::close); worlds.clear(); }
}
