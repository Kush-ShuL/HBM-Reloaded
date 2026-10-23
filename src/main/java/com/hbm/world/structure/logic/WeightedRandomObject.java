package com.hbm.world.structure.logic;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Source object/weight and copied-stack contract, with deferred actual-registry binding for source symbols. */
public final class WeightedRandomObject {
    public final int itemWeight;
    public final Object item;
    public WeightedRandomObject(Object item, int weight) { this.item = item; itemWeight = weight; }
    public ItemStack asStack() {
        if (item instanceof ItemStack stack) return stack.copy();
        if (item instanceof SourceItem symbol) return new ItemStack(symbol.require());
        return ItemStack.EMPTY;
    }
    public Item asItem() { return item instanceof Item value ? value : null; }
    public record SourceItem(String symbol, String id) {
        public Item require() { return LogicDependencies.item(symbol, id); }
        public boolean ready() { return LogicDependencies.hasItem(id); }
    }
}
