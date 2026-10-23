package com.hbm.fluid.network;

import com.hbm.fluid.FluidTank;
import com.hbm.fluid.HbmFluidType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface FluidUser {
    int HIGHEST_VALID_PRESSURE = 5;
    int[] DEFAULT_PRESSURE_RANGE = {0, 0};
    FluidTank[] getAllTanks();
    default boolean canConnect(HbmFluidType type, Direction side) { return side != null; }
    default boolean isLoaded() { return !(this instanceof BlockEntity block) || (!block.isRemoved() && block.hasLevel() && block.getLevel().hasChunkAt(block.getBlockPos())); }
}
