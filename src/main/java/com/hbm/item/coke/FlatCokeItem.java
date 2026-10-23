package com.hbm.item.coke;
import com.hbm.item.FlatSourceItem;
import net.minecraft.world.item.Item;
/** Fixed source coke kind. All fuel semantics remain in HbmFuels/ModuleBurnTime, not a second override. */
public final class FlatCokeItem extends Item implements FlatSourceItem {
    FlatCokeItem(){super(new Properties().stacksTo(64));}
}
