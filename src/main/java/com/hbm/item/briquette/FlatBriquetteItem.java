package com.hbm.item.briquette;
import com.hbm.item.FlatSourceItem;
import net.minecraft.world.item.Item;
/** Fixed original briquette kind; HbmFuels/ModuleBurnTime remain the single fuel backend. */
public final class FlatBriquetteItem extends Item implements FlatSourceItem {
    FlatBriquetteItem(){super(new Properties().stacksTo(64));}
}
