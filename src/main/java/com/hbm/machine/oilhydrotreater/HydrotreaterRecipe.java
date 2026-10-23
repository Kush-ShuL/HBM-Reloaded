package com.hbm.machine.oilhydrotreater;

import com.hbm.recipe.generic.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/** Actual per-100mB operation; the source machine performs its catalyst-gated two-tick batch. */
public record HydrotreaterRecipe(GenericRecipeData data) implements Recipe<RecipeInput>,GenericMachineRecipe {
    public static final MapCodec<HydrotreaterRecipe> CODEC=GenericRecipeData.MAP_CODEC.xmap(HydrotreaterRecipe::new,HydrotreaterRecipe::data);
    @Override public boolean matches(RecipeInput input,Level level){return false;}
    @Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().collapse(net.minecraft.util.RandomSource.create());}
    @Override public ItemStack getResultItem(HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().pool().getFirst().stack();}
    @Override public boolean canCraftInDimensions(int width,int height){return true;}@Override public boolean isSpecial(){return true;}
    @Override public NonNullList<Ingredient> getIngredients(){var ingredients=NonNullList.<Ingredient>create();inputItems().forEach(input->ingredients.add(input.ingredient()));return ingredients;}
    @Override public RecipeType<?> getType(){return HydrotreaterContent.TYPE.get();}@Override public RecipeSerializer<?> getSerializer(){return HydrotreaterContent.SERIALIZER.get();}
    public static final class Serializer implements RecipeSerializer<HydrotreaterRecipe>{
        @Override public MapCodec<HydrotreaterRecipe> codec(){return CODEC;}
        @Override public StreamCodec<RegistryFriendlyByteBuf,HydrotreaterRecipe> streamCodec(){return GenericRecipeData.STREAM_CODEC.map(HydrotreaterRecipe::new,HydrotreaterRecipe::data);}
    }
}
