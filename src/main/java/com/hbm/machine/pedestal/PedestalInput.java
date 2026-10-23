package com.hbm.machine.pedestal;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
public record PedestalInput(List<ItemStack> stacks,BlockPos center) implements RecipeInput {
    public PedestalInput{stacks=List.copyOf(stacks);center=center.immutable();}
    @Override public int size(){return stacks.size();}
    @Override public ItemStack getItem(int slot){return stacks.get(slot);}
}
