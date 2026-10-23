package com.hbm.weapon.sedna.gun;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

/** Actual ammunition-bag item implementations expose their real persistent inventory through this narrow shared path. */
public interface AmmoInventoryProvider {
    Container ammoInventory(ItemStack bag, net.minecraft.core.HolderLookup.Provider registryAccess);
    boolean infiniteAmmo(ItemStack bag);
}
