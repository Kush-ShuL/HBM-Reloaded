package com.hbm.machine.refinery;

import com.hbm.recipe.generic.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/** Native synchronized positional recipe; the shared processor also checks all six fluid lanes. */
public record RefineryRecipe(GenericRecipeData data) implements Recipe<RecipeInput>,GenericMachineRecipe {
    public static final MapCodec<RefineryRecipe> CODEC=GenericRecipeData.MAP_CODEC.xmap(RefineryRecipe::new,RefineryRecipe::data);
    @Override public boolean matches(RecipeInput input,Level level){if(input.size()<inputItems().size())return false;for(int i=0;i<inputItems().size();i++)if(!inputItems().get(i).matches(input.getItem(i),false))return false;return true;}
    @Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().collapse(net.minecraft.util.RandomSource.create());}
    @Override public ItemStack getResultItem(HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().pool().getFirst().stack();}
    @Override public boolean canCraftInDimensions(int width,int height){return true;}@Override public boolean isSpecial(){return true;}
    @Override public NonNullList<Ingredient> getIngredients(){var ingredients=NonNullList.<Ingredient>create();inputItems().forEach(input->ingredients.add(input.ingredient()));return ingredients;}
    @Override public RecipeType<?> getType(){return RefineryContent.TYPE.get();}@Override public RecipeSerializer<?> getSerializer(){return RefineryContent.SERIALIZER.get();}
    public ItemStack solid(){return data.outputItems().isEmpty()?ItemStack.EMPTY:data.outputItems().getFirst().single();}
    public static final class Serializer implements RecipeSerializer<RefineryRecipe>{
        @Override public MapCodec<RefineryRecipe> codec(){return CODEC;}
        @Override public StreamCodec<RegistryFriendlyByteBuf,RefineryRecipe> streamCodec(){return GenericRecipeData.STREAM_CODEC.map(RefineryRecipe::new,RefineryRecipe::data);}
    }
}
