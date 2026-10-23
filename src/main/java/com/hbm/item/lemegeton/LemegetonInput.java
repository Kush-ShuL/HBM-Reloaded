package com.hbm.item.lemegeton;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record LemegetonInput(ItemStack item) implements RecipeInput {
    @Override public ItemStack getItem(int index) { if (index != 0) throw new IndexOutOfBoundsException(index); return item; }
    @Override public int size() { return 1; }
}
