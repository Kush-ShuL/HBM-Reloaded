package com.hbm.machine.compressor;
import com.hbm.recipe.generic.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
/** True synchronized special compression recipes; generic pressure+1 remains machine algorithm. */
public record CompressorRecipe(GenericRecipeData data) implements Recipe<RecipeInput>,GenericMachineRecipe {
 public static final MapCodec<CompressorRecipe> CODEC=GenericRecipeData.MAP_CODEC.xmap(CompressorRecipe::new,CompressorRecipe::data);
 @Override public boolean matches(RecipeInput in,Level level){return false;}
 @Override public ItemStack assemble(RecipeInput in,HolderLookup.Provider lookup){return ItemStack.EMPTY;}
 @Override public ItemStack getResultItem(HolderLookup.Provider lookup){return ItemStack.EMPTY;}
 @Override public boolean canCraftInDimensions(int width,int height){return true;}@Override public boolean isSpecial(){return true;}
 @Override public RecipeType<?> getType(){return CompressorContent.TYPE.get();}@Override public RecipeSerializer<?> getSerializer(){return CompressorContent.SERIALIZER.get();}
 public static final class Serializer implements RecipeSerializer<CompressorRecipe>{@Override public MapCodec<CompressorRecipe> codec(){return CODEC;}@Override public StreamCodec<RegistryFriendlyByteBuf,CompressorRecipe> streamCodec(){return GenericRecipeData.STREAM_CODEC.map(CompressorRecipe::new,CompressorRecipe::data);}}
}
