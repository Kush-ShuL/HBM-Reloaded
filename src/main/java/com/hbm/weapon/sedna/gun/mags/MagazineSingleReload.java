package com.hbm.weapon.sedna.gun.mags;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
/** Original individual-shell magazine: one round per source reload action. */
public final class MagazineSingleReload extends MagazineSingleTypeBase {
    public MagazineSingleReload(int index,int capacity){super(index,capacity);}
    @Override public void reloadAction(ItemStack stack,Container inventory){standardReload(stack,inventory,1);}
}
