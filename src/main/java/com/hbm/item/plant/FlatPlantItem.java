package com.hbm.item.plant;
import com.hbm.item.FlatSourceItem;
import net.minecraft.world.item.Item;
/** The original ordinary ItemEnumMulti plant materials, each with a physical identity. */
public final class FlatPlantItem extends Item implements FlatSourceItem {
    FlatPlantItem(){super(new Properties().stacksTo(64));}
}
