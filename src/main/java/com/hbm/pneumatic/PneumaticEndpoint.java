package com.hbm.pneumatic;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
/** Actual tube filter/position contract used by the original network send algorithm. */
public interface PneumaticEndpoint {BlockPos pneumaticPosition();boolean matchesFilter(ItemStack stack);boolean whitelist();}
