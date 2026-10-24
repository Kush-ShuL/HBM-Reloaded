package com.hbm.item.meteorbook;

import com.hbm.item.StampItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Eight fixed source printing plates: reusable, maxDamage0, no editable contents. */
public final class PrintingStampItem extends StampItem {
    public PrintingStampItem(StampType type){super(0,1,printingType(type));}
    private static StampType printingType(StampType type){if(type==null||type.ordinal()<StampType.PRINTING1.ordinal()||type.ordinal()>StampType.PRINTING8.ordinal())throw new IllegalArgumentException("Not a printing stamp type");return type;}
    @Override public StampType getStampType(Item item,int metadata){return stampType();}
    @Override public StampType stampType(ItemStack stack){return stampType();}
}
