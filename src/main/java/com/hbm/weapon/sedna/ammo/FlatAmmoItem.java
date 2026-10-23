package com.hbm.weapon.sedna.ammo;

import net.minecraft.world.item.Item;

/** One actual ammunition identity. Original ordinal is factory/source data, never a stack component. */
public final class FlatAmmoItem extends Item implements com.hbm.item.FlatSourceItem {
    public final String sourceFamily,variant;
    public final int sourceOrdinal;
    public FlatAmmoItem(String sourceFamily,String variant,int sourceOrdinal,int stackSize){super(new Properties().stacksTo(stackSize));this.sourceFamily=sourceFamily;this.variant=variant;this.sourceOrdinal=sourceOrdinal;}
    public boolean standard(){return sourceFamily.equals("ammo_standard");}
}
