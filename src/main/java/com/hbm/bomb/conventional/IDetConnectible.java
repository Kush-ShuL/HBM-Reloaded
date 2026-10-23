package com.hbm.bomb.conventional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;

/** Original detonation-cord visual connector, deliberately independent of power/fluid networks. */
public interface IDetConnectible {
    default boolean canConnectToDetCord(BlockGetter level, BlockPos pos, Direction face) { return true; }
    static boolean isConnectible(BlockGetter level, BlockPos pos, Direction face) {
        return level.getBlockState(pos).getBlock() instanceof IDetConnectible connector && connector.canConnectToDetCord(level, pos, face);
    }
}
