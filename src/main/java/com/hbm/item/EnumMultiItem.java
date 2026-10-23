package com.hbm.item;

import com.hbm.hazard.HbmHazards;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

/** Exact ItemEnumMulti naming, modulo metadata lookup and variant order semantics. */
public class EnumMultiItem extends Item {
    private final String id;
    private final boolean multiName;
    private final List<String> names;
    private final List<Integer> order;

    public EnumMultiItem(String id, int stackSize, boolean multiName, List<String> names, List<Integer> order) {
        super(new Properties().stacksTo(stackSize).component(HbmHazards.LEGACY_METADATA.get(), 0));
        this.id = id;
        this.multiName = multiName;
        this.names = List.copyOf(names);
        this.order = List.copyOf(order);
    }

    public int ordinal(ItemStack stack) {
        return Math.abs(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0) % names.size());
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        return "item.hbm." + id + (multiName ? "." + names.get(ordinal(stack)) : "");
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable("item." + id + (multiName ? "." + names.get(ordinal(stack)) : "") + ".name");
    }

    public List<ItemStack> creativeStacks() {
        return order.stream().map(index -> {
            ItemStack stack = new ItemStack(this);
            stack.set(HbmHazards.LEGACY_METADATA.get(), index);
            return stack;
        }).toList();
    }
}
