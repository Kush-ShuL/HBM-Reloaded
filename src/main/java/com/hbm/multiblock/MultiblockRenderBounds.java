package com.hbm.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** World-space bounds cover the complete rotated structure, including its core block. */
public final class MultiblockRenderBounds {
    private MultiblockRenderBounds() { }

    public static AABB of(BlockPos core, BlockState state) {
        if (!(state.getBlock() instanceof DummyableBlock block) || !DummyableBlock.isCore(state)) return new AABB(core);
        int[] dimensions = DummyableBlock.rotate(block.dimensions(), DummyableBlock.facing(state));
        return new AABB(core.getX() - dimensions[4], core.getY() - dimensions[1], core.getZ() - dimensions[2],
                core.getX() + dimensions[5] + 1, core.getY() + dimensions[0] + 1, core.getZ() + dimensions[3] + 1);
    }
}
