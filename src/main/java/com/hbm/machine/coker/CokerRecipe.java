package com.hbm.machine.coker;

import com.hbm.recipe.generic.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/** Native synchronized Coker recipe;20000TU job progress is not a fictional constant tick duration. */
public record CokerRecipe(GenericRecipeData data) implements Recipe<RecipeInput>,GenericMachineRecipe {
    public com.hbm.fluid.FluidStack input(){return data.inputFluids().getFirst();}public ItemStack solid(){return data.outputItems().isEmpty()?ItemStack.EMPTY:data.outputItems().getFirst().pool().getFirst().stack();}public com.hbm.fluid.FluidStack byproduct(){return data.outputFluids().isEmpty()?null:data.outputFluids().getFirst();}
    public static final MapCodec<CokerRecipe> CODEC=GenericRecipeData.MAP_CODEC.xmap(CokerRecipe::new,CokerRecipe::data);
    @Override public boolean matches(RecipeInput input,Level level){if(input.size()<inputItems().size())return false;for(int i=0;i<inputItems().size();i++)if(!inputItems().get(i).matches(input.getItem(i),false))return false;return true;}
    @Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().collapse(net.minecraft.util.RandomSource.create());}
    @Override public ItemStack getResultItem(HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().pool().getFirst().stack();}
    @Override public boolean canCraftInDimensions(int width,int height){return true;}@Override public boolean isSpecial(){return true;}
    @Override public NonNullList<Ingredient> getIngredients(){var ingredients=NonNullList.<Ingredient>create();inputItems().forEach(input->ingredients.add(input.ingredient()));return ingredients;}
    @Override public RecipeType<?> getType(){return CokerContent.TYPE.get();}@Override public RecipeSerializer<?> getSerializer(){return CokerContent.SERIALIZER.get();}
    public static final class Serializer implements RecipeSerializer<CokerRecipe>{
        @Override public MapCodec<CokerRecipe> codec(){return CODEC;}
        @Override public StreamCodec<RegistryFriendlyByteBuf,CokerRecipe> streamCodec(){return GenericRecipeData.STREAM_CODEC.map(CokerRecipe::new,CokerRecipe::data);}
    }
}
