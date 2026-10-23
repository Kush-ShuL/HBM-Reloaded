package com.hbm.block.crt;

import com.hbm.hazard.HbmHazards;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

/** Actual ItemBlockBase subtype metadata; all four source screen variants share the original item ID/name. */
public final class DecoCRTItem extends BlockItem {
    public DecoCRTItem(DecoCRTBlock block) { super(block, new Properties()); }
    public static int variant(ItemStack stack) { return Math.abs(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0) % 4); }
    public static ItemStack stack(int variant) {
        var stack = new ItemStack(DecoCRTContent.ITEM.get());
        stack.set(HbmHazards.LEGACY_METADATA.get(), Math.abs(variant % 4));
        return stack;
    }
}
