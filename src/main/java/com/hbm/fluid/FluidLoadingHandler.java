package com.hbm.fluid;

import net.minecraft.world.item.ItemStack;

public interface FluidLoadingHandler {
    boolean fillItem(ItemStack[] slots, int in, int out, FluidTank tank);
    boolean emptyItem(ItemStack[] slots, int in, int out, FluidTank tank);
}
