package com.hbm.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.Heightmap;

/** The source heightMap stops at positive light opacity, rather than movement collision. */
public final class SourceWorldHeight {
    public static int get(LevelReader world, int x, int z) {
        var cursor = new BlockPos.MutableBlockPos(x, world.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1, z);
        for (int y = cursor.getY(); y >= world.getMinBuildHeight(); y--) {
            cursor.setY(y);
            if (world.getBlockState(cursor).getLightBlock(world, cursor) > 0) return y + 1;
        }
        return world.getMinBuildHeight();
    }

    private SourceWorldHeight() {}
}
