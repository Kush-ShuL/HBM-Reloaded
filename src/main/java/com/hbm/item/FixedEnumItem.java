package com.hbm.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** One registered source enum value. Mutable machine/item state belongs to its actual owner. */
public final class FixedEnumItem extends Item implements FlatSourceItem {
    private final String family;
    private final String variant;
    private final int sourceOrdinal;
    private final boolean multiName;

    public FixedEnumItem(String family, String variant, int sourceOrdinal, int stackSize, boolean multiName) {
        super(new Properties().stacksTo(stackSize));
        this.family = family;
        this.variant = variant;
        this.sourceOrdinal = sourceOrdinal;
        this.multiName = multiName;
    }

    public String family() { return family; }
    public String variant() { return variant; }
    public int sourceOrdinal() { return sourceOrdinal; }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable("item." + family + (multiName ? "." + variant : "") + ".name");
    }

    public List<ItemStack> creativeStacks() { return List.of(new ItemStack(this)); }
}
