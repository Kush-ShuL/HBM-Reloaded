package com.hbm.block.ore;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

/** A native fixed block item; its authored name is shared with the corresponding block. */
public final class ResourceOreItem extends BlockItem {
    public ResourceOreItem(ResourceOreBlock block) { super(block, new Properties()); }
    @Override public String getDescriptionId(ItemStack stack) { return ((ResourceOreBlock)getBlock()).descriptionId(); }
}
