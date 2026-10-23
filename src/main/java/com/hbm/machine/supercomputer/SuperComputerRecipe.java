package com.hbm.machine.supercomputer;
import com.hbm.recipe.generic.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
/** Actual source GenericRecipe data, including true weighted successful/broken drive alternatives. */
public record SuperComputerRecipe(GenericRecipeData data) implements Recipe<RecipeInput>,GenericMachineRecipe {
 public static final MapCodec<SuperComputerRecipe> CODEC=GenericRecipeData.MAP_CODEC.xmap(SuperComputerRecipe::new,SuperComputerRecipe::data);
 @Override public boolean matches(RecipeInput input,Level level){return true;}@Override public boolean canCraftInDimensions(int width,int height){return true;}@Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().collapse(net.minecraft.util.RandomSource.create());}@Override public ItemStack getResultItem(HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().pool().getFirst().stack();}@Override public boolean isSpecial(){return true;}@Override public RecipeType<?> getType(){return SuperComputerRecipes.TYPE.get();}@Override public RecipeSerializer<?> getSerializer(){return SuperComputerRecipes.SERIALIZER.get();}
 public static final class Serializer implements RecipeSerializer<SuperComputerRecipe>{public MapCodec<SuperComputerRecipe> codec(){return CODEC;}public StreamCodec<RegistryFriendlyByteBuf,SuperComputerRecipe> streamCodec(){return StreamCodec.of((buf,recipe)->recipe.data.encode(buf),buf->new SuperComputerRecipe(GenericRecipeData.decode(buf)));}}
}
