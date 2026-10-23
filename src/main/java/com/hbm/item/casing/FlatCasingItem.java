package com.hbm.item.casing;

import com.hbm.item.FlatSourceItem;
import net.minecraft.world.item.Item;

/** Real physical source casing. Fixed source identity is used only by the unchanged fractional recovery algorithm. */
public final class FlatCasingItem extends Item implements FlatSourceItem {
    private final int sourceOrdinal;
    FlatCasingItem(int sourceOrdinal){super(new Properties().stacksTo(64));this.sourceOrdinal=sourceOrdinal;}
    public int sourceOrdinal(){return sourceOrdinal;}
    public String sourceRecoveryKey(){return "item.casing."+FlatCasingContent.NAMES.get(sourceOrdinal)+"@"+sourceOrdinal;}
}
