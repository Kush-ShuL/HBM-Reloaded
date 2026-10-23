package com.hbm.weapon.sedna.gun.mags;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public final class MagazineFullReload extends MagazineSingleTypeBase {
    public MagazineFullReload(int index,int capacity){super(index,capacity);}
    @Override public void reloadAction(ItemStack stack,Container inventory){standardReload(stack,inventory,capacity);}
}
