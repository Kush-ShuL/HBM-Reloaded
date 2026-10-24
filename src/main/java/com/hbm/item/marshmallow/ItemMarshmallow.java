package com.hbm.item.marshmallow;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Fixed raw or roasted stick identity supplied by its registry entry; not an edible ItemFood. */
public final class ItemMarshmallow extends Item {
    public ItemMarshmallow() { super(new Properties().stacksTo(1)); }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item.marshmallow.name"); }
}
