package com.hbm.worldgen.surface;

import com.hbm.config.WorldgenControls;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;

/** The one source flower/reeds scatter loop, extracted from the admitted506 feature. */
public final class SurfaceFlowerScatter {
    public static boolean scatter(WorldGenLevel level,RandomSource random,int chunkX,int chunkZ,
                                  BlockState state,WorldgenControls.Rule control) {
        // The old population offset matters: do not recenter or clamp the patch to its owner chunk.
        int centerX = (chunkX << 4) + random.nextInt(16) + 8;
        int centerZ = (chunkZ << 4) + random.nextInt(16) + 8;
        var center = readyChunk(level, chunkX, chunkZ, centerX, centerZ);
        if (center == null) return false;
        int centerY = center.getHeight(Heightmap.Types.WORLD_SURFACE, centerX & 15, centerZ & 15) + 1;
        int low = Math.max(level.getMinBuildHeight(), control.integer("minY", level.getMinBuildHeight()));
        int high = Math.min(level.getMaxBuildHeight() - 1, control.integer("maxY", level.getMaxBuildHeight() - 1));
        var pos = new BlockPos.MutableBlockPos();
        boolean changed = false;
        for (int attempt = 0; attempt < 64; attempt++) {
            // Keep all six original random draws, even if this position cannot be written.
            int x = centerX + random.nextInt(8) - random.nextInt(8);
            int y = centerY + random.nextInt(4) - random.nextInt(4);
            int z = centerZ + random.nextInt(8) - random.nextInt(8);
            if (y < low || y > high || y <= level.getMinBuildHeight()
                    || level.getLevel().dimensionType().hasCeiling() && y >= level.getMaxBuildHeight() - 1) continue;
            pos.set(x, y, z);
            var chunk = readyChunk(level, chunkX, chunkZ, x, z);
            if (chunk == null || !level.ensureCanWrite(pos) || !chunk.getBlockState(pos).isAir()) continue;
            // Use the caller block's real survival rule, without replacing its soil or water.
            if (state.canSurvive(level, pos)) changed |= level.setBlock(pos, state, Block.UPDATE_CLIENTS);
        }
        return changed;
    }

    private static ChunkAccess readyChunk(WorldGenLevel level, int ownerX, int ownerZ, int x, int z) {
        int chunkX = x >> 4, chunkZ = z >> 4;
        // Native FEATURES supplies CARVERS-ready neighbours at distance 1 and permits only these writes.
        if (Math.abs(chunkX - ownerX) > 1 || Math.abs(chunkZ - ownerZ) > 1
                || !level.hasChunk(chunkX, chunkZ)) return null;
        if (level instanceof WorldGenRegion) return level.getChunk(chunkX, chunkZ, ChunkStatus.CARVERS, false);
        // /place feature and other direct callers may use ServerLevel: never request or wait for a new chunk.
        return level.getLevel().getChunkSource().getChunkNow(chunkX, chunkZ);
    }

    private SurfaceFlowerScatter() { }
}
