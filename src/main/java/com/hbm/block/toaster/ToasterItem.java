package com.hbm.block.toaster;

import com.hbm.hazard.HbmHazards;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

/** All three original ItemBlockBase variants deliberately share the one source name. */
public final class ToasterItem extends BlockItem {
    public ToasterItem(ToasterBlock block) { super(block, new Properties().component(HbmHazards.LEGACY_METADATA.get(), 0)); }
    public static int variant(ItemStack stack) { return Math.abs(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0) % 3); }
    public static ItemStack stack(int metadata) {
        var stack = new ItemStack(ToasterContent.ITEM.get()); stack.set(HbmHazards.LEGACY_METADATA.get(), Math.abs(metadata % 3)); return stack;
    }
}
