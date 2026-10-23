package com.hbm.missile.custom.launcher;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** Source compact launcher deliberately has no OC/CC component. */
public final class CompactLauncherEntity extends CustomLauncherEntity {
    public CompactLauncherEntity(BlockPos pos, BlockState state) { super(CustomLauncherContent.COMPACT_ENTITY.get(), pos, state, false); }
}
