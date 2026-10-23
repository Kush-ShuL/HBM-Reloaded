package com.hbm.block.ore;

import com.hbm.registry.HbmContent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** New host identity with the original behavior, hazards and processing identity available separately. */
public final class DeepslateOreItem extends LegacyOreItem {
    public DeepslateOreItem(LegacyOreBlock block) { super(block); }
    public String originalSymbol() { return spec().id(); }
    public Item originalItem() { return HbmContent.item(originalSymbol()); }
    public ItemStack originalStack(ItemStack stack) { return stack.transmuteCopy(originalItem()); }
    @Override public String getDescriptionId(ItemStack stack) { return getBlock().getDescriptionId(); }
}
