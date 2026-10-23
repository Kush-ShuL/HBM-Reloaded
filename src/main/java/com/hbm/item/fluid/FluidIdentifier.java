package com.hbm.item.fluid;

import com.hbm.fluid.HbmFluidType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/** World may be null for the original in-container type-selection operation. */
public interface FluidIdentifier {
    HbmFluidType getType(@Nullable Level level, BlockPos position, ItemStack stack);
}
