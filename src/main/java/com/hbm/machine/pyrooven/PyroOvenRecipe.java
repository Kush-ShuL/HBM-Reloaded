package com.hbm.machine.pyrooven;

import com.hbm.recipe.generic.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/** Native source ordered pyrolysis recipe with optional solid/fluid inputs and outputs. */
public record PyroOvenRecipe(GenericRecipeData data) implements Recipe<RecipeInput>,GenericMachineRecipe {
    public com.hbm.fluid.FluidStack inputFluid(){return inputFluids().isEmpty()?null:inputFluids().getFirst();}
    public com.hbm.fluid.FluidStack outputFluid(){return outputFluids().isEmpty()?null:outputFluids().getFirst();}
    public com.hbm.recipe.generic.GenericIngredient inputItem(){return inputItems().isEmpty()?null:inputItems().getFirst();}
    public ItemStack outputItem(){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().pool().getFirst().stack();}
    public static final MapCodec<PyroOvenRecipe> CODEC=GenericRecipeData.MAP_CODEC.xmap(PyroOvenRecipe::new,PyroOvenRecipe::data);
    @Override public boolean matches(RecipeInput input,Level level){return false;}
    @Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().collapse(net.minecraft.util.RandomSource.create());}
    @Override public ItemStack getResultItem(HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().pool().getFirst().stack();}
    @Override public boolean canCraftInDimensions(int width,int height){return true;}@Override public boolean isSpecial(){return true;}
    @Override public NonNullList<Ingredient> getIngredients(){var ingredients=NonNullList.<Ingredient>create();inputItems().forEach(input->ingredients.add(input.ingredient()));return ingredients;}
    @Override public RecipeType<?> getType(){return PyroOvenContent.TYPE.get();}@Override public RecipeSerializer<?> getSerializer(){return PyroOvenContent.SERIALIZER.get();}
    public static final class Serializer implements RecipeSerializer<PyroOvenRecipe>{
        @Override public MapCodec<PyroOvenRecipe> codec(){return CODEC;}
        @Override public StreamCodec<RegistryFriendlyByteBuf,PyroOvenRecipe> streamCodec(){return GenericRecipeData.STREAM_CODEC.map(PyroOvenRecipe::new,PyroOvenRecipe::data);}
    }
}
