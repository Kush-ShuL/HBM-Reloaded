package com.hbm.radiation;

import com.hbm.bomb.nuclear.NuclearChunkAccess;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

/** Source PRISM's three-dimensional resistance-aware field; no second exposure or network backend. */
final class PrismRadiationWorld implements RadiationWorld {
    // ForgeDirection.VALID_DIRECTIONS order is significant for float addition.
    private static final Direction[] DIRECTIONS = { Direction.DOWN, Direction.UP, Direction.NORTH,
            Direction.SOUTH, Direction.WEST, Direction.EAST };
    private final ServerLevel level;
    private final RadiationSavedData saved;
    private final Map<ChunkPos, PrismRadiationCell[]> active = new ConcurrentHashMap<>();
    private final NuclearChunkAccess readiness = new NuclearChunkAccess();
    private final int minSection, sectionCount;

    PrismRadiationWorld(ServerLevel level) {
        this.level = level;
        minSection = level.getMinSection();
        sectionCount = level.getSectionsCount();
        saved = RadiationSavedData.get(level);
        saved.selectAlgorithm(RadiationAlgorithm.PRISM, level.dimension().location().toString());
    }

    @Override public void configure(RadiationSettings settings) {}

    @Override public void loadChunk(ChunkPos pos) {
        // ChunkEvent.Load may still precede FULL. Only saved data and section coordinates are touched here.
        active.computeIfAbsent(pos, key -> {
            var cells = new PrismRadiationCell[sectionCount];
            for (int i = 0; i < sectionCount; i++) {
                var cell = saved.prismSection(key.toLong(), minSection + i);
                cells[i] = cell == null ? new PrismRadiationCell() : cell;
            }
            return cells;
        });
    }

    @Override public void unloadChunk(ChunkPos pos) {
        var cells = active.remove(pos);
        if (cells != null) saved.storePrismChunk(pos.toLong(), minSection, cells);
    }

    @Override public float getRadiation(int x, int y, int z) {
        var cells = active.get(new ChunkPos(x >> 4, z >> 4));
        var cell = cells == null ? null : cells[index(y)];
        return cell == null ? 0F : cell.radiation;
    }

    @Override public void setRadiation(int x, int y, int z, float radiation) {
        var pos = new ChunkPos(x >> 4, z >> 4);
        // The common API never synchronously promotes a cold/proto chunk to FULL.
        if (readiness.peekReady(level, pos.x, pos.z) == null) return;
        var cells = active.computeIfAbsent(pos, unused -> new PrismRadiationCell[sectionCount]);
        int i = index(y);
        var cell = ensureCell(cells, i);
        rebuild(pos, i, cell);
        cell.radiation = PrismRadiationCell.clamp(radiation);
        saved.storePrismChunk(pos.toLong(), minSection, cells);
    }

    private int index(int y) { return Math.max(0, Math.min((y >> 4) - minSection, sectionCount - 1)); }

    private static PrismRadiationCell ensureCell(PrismRadiationCell[] cells, int i) {
        if (cells[i] == null) cells[i] = new PrismRadiationCell();
        return cells[i];
    }

    /** Read-only readiness: no tickets, futures, synchronous loads or joins are requested. */
    private LevelChunk readyHalo(ChunkPos pos) {
        LevelChunk center = null;
        for (int dx = -1; dx <= 1; dx++) for (int dz = -1; dz <= 1; dz++) {
            var chunk = readiness.peekReady(level, pos.x + dx, pos.z + dz);
            if (chunk == null) return null;
            if (dx == 0 && dz == 0) center = chunk;
        }
        return center;
    }

    private boolean rebuild(ChunkPos pos, int i, PrismRadiationCell cell) {
        var chunk = readyHalo(pos);
        if (chunk == null) { cell.needsRebuild = true; return false; }
        cell.rebuild(level, chunk, minSection + i);
        saved.setDirty();
        return true;
    }

    @Override public void update(List<RadiationEffects> effects, RadiationSettings settings, int cycle) {
        int allowance = 25;
        for (var entry : active.entrySet()) {
            var pos = entry.getKey();
            var chunk = readiness.peekReady(level, pos.x, pos.z);
            for (int i = 0; i < sectionCount; i++) {
                var cell = entry.getValue()[i];
                if (cell == null) continue;
                boolean rebuilt = false;
                if (allowance > 0 && cell.needsRebuild && rebuild(pos, i, cell)) {
                    allowance--;
                    rebuilt = true;
                }
                // Keep int overflow and Math.abs behaviour of the source coordinate schedule.
                if (!rebuilt && Math.abs(pos.x * pos.z) % 5 == cycle % 5 && chunk != null) {
                    int checksum = PrismRadiationCell.checksum(chunk.getSection(i));
                    if (checksum != cell.checksum) rebuild(pos, i, cell);
                }
                // A fresh region cannot be treated as unshielded while its FULL halo is unavailable.
                // Freeze only that region's existing dose; initialized source caches can remain in use.
                cell.previousRadiation = chunk != null && cell.initialized ? cell.radiation : 0F;
                if (chunk != null && cell.initialized) cell.radiation = 0F;
            }
        }

        // Preserve the source's deferred-addition overwrite behaviour, rather than summing a new stencil.
        var additions = new HashMap<ChunkPos, PrismRadiationCell[]>();
        for (var entry : active.entrySet()) {
            var cells = entry.getValue();
            float previous = 0F;
            for (var cell : cells) if (cell != null) previous += cell.previousRadiation;
            if (previous <= 0F) continue;
            for (int i = 0; i < sectionCount; i++) {
                var cell = cells[i];
                if (cell == null || cell.previousRadiation <= 0F || !Float.isFinite(cell.previousRadiation)) continue;
                float transferred = 0F;
                for (var direction : DIRECTIONS)
                    transferred += spread(cell, i, entry.getKey(), cells, additions, direction);
                cell.radiation += (cell.previousRadiation - transferred) * .95F;
                cell.radiation -= 1F;
                cell.radiation = PrismRadiationCell.clamp(cell.radiation);
            }
        }
        active.putAll(additions);
        for (var entry : active.entrySet()) saved.storePrismChunk(entry.getKey().toLong(), minSection, entry.getValue());
        // No Simple diffusion-write/fog or terrain hook exists on PRISM in the source.
    }

    private float spread(PrismRadiationCell from, int i, ChunkPos origin, PrismRadiationCell[] cells,
                         Map<ChunkPos, PrismRadiationCell[]> additions, Direction movement) {
        float amount = from.previousRadiation * .1F;
        if (amount <= 1F) return 0F;
        PrismRadiationCell to;
        if (movement.getStepY() != 0) {
            int next = i + movement.getStepY();
            if (next < 0 || next == sectionCount) return amount;
            to = ensureCell(cells, next);
            if (!to.initialized && !rebuild(origin, next, to)) return 0F;
        } else {
            var pos = new ChunkPos(origin.x + movement.getStepX(), origin.z + movement.getStepZ());
            if (readiness.peekReady(level, pos.x, pos.z) == null) return amount;
            var target = active.get(pos);
            if (target == null) {
                target = new PrismRadiationCell[sectionCount];
                additions.put(pos, target);
            }
            to = ensureCell(target, i);
            if (!to.initialized && !rebuild(pos, i, to)) return 0F;
        }
        float resistance = from.resistance(movement.getOpposite()) + to.resistance(movement);
        double attenuation = Math.pow(Math.E, -resistance / 10_000D);
        float transferred = (float) Math.min(amount * attenuation, amount);
        to.radiation += transferred;
        return transferred;
    }

    @Override public void clear() { active.clear(); saved.clear(); }
    @Override public void close() { readiness.close(); }
}
