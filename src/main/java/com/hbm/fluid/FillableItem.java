package com.hbm.fluid;

import net.minecraft.world.item.ItemStack;

/** Exact six-operation native-fluid contract from api.hbm.fluidmk2.IFillableItem. */
public interface FillableItem {
    boolean acceptsFluid(HbmFluidType type, ItemStack stack);
    int tryFill(HbmFluidType type, int amount, ItemStack stack);
    boolean providesFluid(HbmFluidType type, ItemStack stack);
    int tryEmpty(HbmFluidType type, int amount, ItemStack stack);
    HbmFluidType getFirstFluidType(ItemStack stack);
    int getFill(ItemStack stack);
}
