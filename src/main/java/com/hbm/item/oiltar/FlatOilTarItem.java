package com.hbm.item.oiltar;
import com.hbm.item.FlatSourceItem;
import net.minecraft.world.item.Item;
/** A physical EnumTarType identity. Processing, dyes and fuel use the existing source registries. */
public final class FlatOilTarItem extends Item implements FlatSourceItem {
    FlatOilTarItem(){super(new Properties().stacksTo(64));}
}
