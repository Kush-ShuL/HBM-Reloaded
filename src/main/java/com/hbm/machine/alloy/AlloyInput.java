package com.hbm.machine.alloy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
public record AlloyInput(ItemStack upper,ItemStack lower) implements RecipeInput {
    @Override public ItemStack getItem(int slot) { return slot==0?upper:slot==1?lower:ItemStack.EMPTY; }
    @Override public int size() { return 2; }
}
