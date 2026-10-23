package com.hbm.item;

import com.hbm.registry.HbmContent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Resolves crafting remainders lazily, after the item registry has been bound. */
public class BasicItem extends Item {
    private final String container;

    public BasicItem(Properties properties, String container) {
        super(properties);
        this.container = container;
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return container != null;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        return container == null ? ItemStack.EMPTY : new ItemStack(HbmContent.item(container));
    }
}
