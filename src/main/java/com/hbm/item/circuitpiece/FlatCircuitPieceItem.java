package com.hbm.item.circuitpiece;
import com.hbm.item.FlatSourceItem;
import net.minecraft.world.item.Item;
/** An ordinary source ItemEnumMulti fragment with one physical identity. */
public final class FlatCircuitPieceItem extends Item implements FlatSourceItem {
    FlatCircuitPieceItem(){super(new Properties().stacksTo(64));}
}
