package com.hbm.item.boronchemistry;

import com.hbm.item.tool.CraftingDegradationItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/** Source ItemCraftingDegradation(0): the existing unique no-wear crafting-remainder implementation. */
public final class BoronChemistrySetItem extends CraftingDegradationItem {
    public BoronChemistrySetItem(){super(0);}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.chemistry_set_boron.name");}
}
