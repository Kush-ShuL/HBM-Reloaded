package com.hbm.machine.exposurechamber;
import com.hbm.recipe.generic.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
/** Actual source particle + ingredient AStacks, with native recipe synchronization. */
public record ExposureChamberRecipe(GenericRecipeData data) implements Recipe<RecipeInput>,GenericMachineRecipe {
 public static final MapCodec<ExposureChamberRecipe> CODEC=GenericRecipeData.MAP_CODEC.xmap(ExposureChamberRecipe::new,ExposureChamberRecipe::data);
 @Override public boolean matches(RecipeInput input,Level level){if(input.size()<inputItems().size())return false;for(int i=0;i<inputItems().size();i++)if(!inputItems().get(i).matches(input.getItem(i),true))return false;return true;}
 @Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().pool().getFirst().stack().copy();}
 @Override public ItemStack getResultItem(HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().pool().getFirst().stack();}
 @Override public boolean canCraftInDimensions(int width,int height){return true;}@Override public boolean isSpecial(){return true;}
 @Override public NonNullList<Ingredient> getIngredients(){var result=NonNullList.<Ingredient>create();inputItems().forEach(in->result.add(in.ingredient()));return result;}
 @Override public RecipeType<?> getType(){return ExposureChamberContent.TYPE.get();}@Override public RecipeSerializer<?> getSerializer(){return ExposureChamberContent.SERIALIZER.get();}
 public static final class Serializer implements RecipeSerializer<ExposureChamberRecipe>{@Override public MapCodec<ExposureChamberRecipe> codec(){return CODEC;}@Override public StreamCodec<RegistryFriendlyByteBuf,ExposureChamberRecipe> streamCodec(){return GenericRecipeData.STREAM_CODEC.map(ExposureChamberRecipe::new,ExposureChamberRecipe::data);}}
}
