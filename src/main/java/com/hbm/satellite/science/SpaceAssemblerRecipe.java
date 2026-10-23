package com.hbm.satellite.science;

import com.hbm.recipe.generic.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/** Genuine source GenericRecipeNoPower; all output pools and source array/config grammar remain shared GenericRecipe data. */
public record SpaceAssemblerRecipe(GenericRecipeData data) implements Recipe<RecipeInput>,GenericMachineRecipe {
    public static final MapCodec<SpaceAssemblerRecipe> CODEC=GenericRecipeData.MAP_CODEC.xmap(SpaceAssemblerRecipe::new,SpaceAssemblerRecipe::data);
    @Override public boolean matches(RecipeInput input,Level level){return inputItems().size()==1&&input.size()>0&&inputItems().getFirst().matches(input.getItem(0),true);}
    @Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:SpaceAssemblerRecipes.collapse(outputItems().getFirst());}
    @Override public ItemStack getResultItem(HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().pool().getFirst().stack();}
    @Override public boolean canCraftInDimensions(int width,int height){return true;}
    @Override public boolean isSpecial(){return true;}
    @Override public NonNullList<Ingredient> getIngredients(){var result=NonNullList.<Ingredient>create();inputItems().forEach(in->result.add(in.ingredient()));return result;}
    @Override public RecipeType<?> getType(){return ScienceSatelliteContent.TYPE.get();}
    @Override public RecipeSerializer<?> getSerializer(){return ScienceSatelliteContent.SERIALIZER.get();}
    public static final class Serializer implements RecipeSerializer<SpaceAssemblerRecipe> {
        @Override public MapCodec<SpaceAssemblerRecipe> codec(){return CODEC;}
        @Override public StreamCodec<RegistryFriendlyByteBuf,SpaceAssemblerRecipe> streamCodec(){return GenericRecipeData.STREAM_CODEC.map(SpaceAssemblerRecipe::new,SpaceAssemblerRecipe::data);}
    }
}
