package com.hbm.machine.reactor.fuelpool;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Narrow binding for the genuine future ItemRBMKRod. No imitation rods are registered.
 * A real rod must execute original updateHeat(level,stack,.025) then provideHeat(level,stack,20,.025).
 * Heat belongs to the stack itself, not a second cooling inventory or timer. */
public interface FuelPoolRod {
    double fuelPoolCoreHeat(ItemStack stack);
    double fuelPoolHullHeat(ItemStack stack);
    void coolInFuelPool(Level level,ItemStack stack);
}
