package com.hbm.item.ash;
import com.hbm.item.FlatSourceItem;
import net.minecraft.world.item.Item;
/** A physical source ash identity; hazards, dyes and fuel remain in the existing shared registries. */
public final class FlatAshItem extends Item implements FlatSourceItem {
    FlatAshItem(){super(new Properties().stacksTo(64));}
}
