package com.hbm.item.marshmallow;

import com.hbm.hazard.HbmHazards;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Actual two source ItemMarshmallow variants; a stick, not an edible ItemFood. */
public final class ItemMarshmallow extends Item {
    public ItemMarshmallow() { super(new Properties().stacksTo(1)); }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item.marshmallow.name"); }
    public static int metadata(ItemStack stack) { return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0); }
    public ItemStack stack(int metadata) {
        var stack = new ItemStack(this);
        stack.set(HbmHazards.LEGACY_METADATA.get(), metadata);
        return stack;
    }
}
