package com.hbm.machine.oilcatalyticcracker;
import com.hbm.recipe.generic.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
/** Actual100mB source input, fixed200steam and2spentsteam; both output tuple slots survive even when NONE. */
public record CatalyticCrackerRecipe(GenericRecipeData data) implements Recipe<RecipeInput>,GenericMachineRecipe {
 public static final MapCodec<CatalyticCrackerRecipe> CODEC=GenericRecipeData.MAP_CODEC.xmap(CatalyticCrackerRecipe::new,CatalyticCrackerRecipe::data);
 @Override public boolean matches(RecipeInput input,Level level){return false;}@Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return ItemStack.EMPTY;}@Override public ItemStack getResultItem(HolderLookup.Provider lookup){return ItemStack.EMPTY;}@Override public boolean canCraftInDimensions(int width,int height){return true;}@Override public boolean isSpecial(){return true;}@Override public RecipeType<?> getType(){return CatalyticCrackerContent.TYPE.get();}@Override public RecipeSerializer<?> getSerializer(){return CatalyticCrackerContent.SERIALIZER.get();}
 public static final class Serializer implements RecipeSerializer<CatalyticCrackerRecipe>{@Override public MapCodec<CatalyticCrackerRecipe> codec(){return CODEC;}@Override public StreamCodec<RegistryFriendlyByteBuf,CatalyticCrackerRecipe> streamCodec(){return GenericRecipeData.STREAM_CODEC.map(CatalyticCrackerRecipe::new,CatalyticCrackerRecipe::data);}}
}
