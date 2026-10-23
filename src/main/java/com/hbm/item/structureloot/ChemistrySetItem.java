package com.hbm.item.structureloot;

import com.hbm.item.tool.CraftingDegradationItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/** The existing actual source crafting-degradation contract, including staying in the grid and break-after100. */
public final class ChemistrySetItem extends CraftingDegradationItem {
    public ChemistrySetItem(){super(100);}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.chemistry_set.name");}
}
