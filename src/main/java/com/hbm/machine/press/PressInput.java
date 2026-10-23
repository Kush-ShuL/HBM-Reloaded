package com.hbm.machine.press;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
public record PressInput(ItemStack material, ItemStack stamp) implements RecipeInput {
    @Override public ItemStack getItem(int slot) { return slot == 0 ? material : slot == 1 ? stamp : ItemStack.EMPTY; }
    @Override public int size() { return 2; }
}
