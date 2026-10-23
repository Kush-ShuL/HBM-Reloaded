package com.hbm.item.ingotmetal;
import com.hbm.item.FlatSourceItem;
import net.minecraft.world.item.Item;
/** One hidden source EnumIngotMetal identity; not a material-id-bearing ingot. */
public final class FlatIngotMetalItem extends Item implements FlatSourceItem {
    FlatIngotMetalItem(){super(new Properties().stacksTo(64));}
}
