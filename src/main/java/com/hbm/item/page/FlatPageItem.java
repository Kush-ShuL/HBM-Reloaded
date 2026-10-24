package com.hbm.item.page;
import com.hbm.item.FlatSourceItem;
import net.minecraft.world.item.Item;
/** One ordinary source page; the source defines no use action or mutable page state. */
public final class FlatPageItem extends Item implements FlatSourceItem {
    FlatPageItem(){super(new Properties().stacksTo(1));}
}
