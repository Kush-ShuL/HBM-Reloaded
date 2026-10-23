package com.hbm.machine.radiolysis;

import com.hbm.recipe.generic.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/** Native synchronized positional recipe; the shared processor also checks all four fluid lanes. */
public record RadiolysisRecipe(GenericRecipeData data) implements Recipe<RecipeInput>,GenericMachineRecipe {
    public static final MapCodec<RadiolysisRecipe> CODEC=GenericRecipeData.MAP_CODEC.xmap(RadiolysisRecipe::new,RadiolysisRecipe::data);
    @Override public boolean matches(RecipeInput input,Level level){if(input.size()<inputItems().size())return false;for(int i=0;i<inputItems().size();i++)if(!inputItems().get(i).matches(input.getItem(i),false))return false;return true;}
    @Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().collapse(net.minecraft.util.RandomSource.create());}
    @Override public ItemStack getResultItem(HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().pool().getFirst().stack();}
    @Override public boolean canCraftInDimensions(int width,int height){return true;}@Override public boolean isSpecial(){return true;}
    @Override public NonNullList<Ingredient> getIngredients(){var ingredients=NonNullList.<Ingredient>create();inputItems().forEach(input->ingredients.add(input.ingredient()));return ingredients;}
    @Override public RecipeType<?> getType(){return RadiolysisContent.TYPE.get();}@Override public RecipeSerializer<?> getSerializer(){return RadiolysisContent.SERIALIZER.get();}
    public static final class Serializer implements RecipeSerializer<RadiolysisRecipe>{
        @Override public MapCodec<RadiolysisRecipe> codec(){return CODEC;}
        @Override public StreamCodec<RegistryFriendlyByteBuf,RadiolysisRecipe> streamCodec(){return GenericRecipeData.STREAM_CODEC.map(RadiolysisRecipe::new,RadiolysisRecipe::data);}
    }
}
