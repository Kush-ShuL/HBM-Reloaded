package com.hbm.weapon.sedna.gun;

import net.minecraft.world.item.ItemStack;

/** Implemented by actual casing-bag items when admitted; black-powder ammunition has no casing definition. */
public interface CasingRecovery {
    boolean pushCasing(ItemStack bag, ItemStack casing, float amount, net.minecraft.core.HolderLookup.Provider lookup);
}
