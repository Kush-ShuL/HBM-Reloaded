package com.hbm.machine.anvil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
public record AnvilInput(ItemStack left,ItemStack right) implements RecipeInput {
    @Override public ItemStack getItem(int index) { return index==0?left:right; }
    @Override public int size() { return 2; }
}
