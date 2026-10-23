package com.hbm.machine.precisionassembler;
import com.hbm.recipe.generic.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
/** Actual native positional generic recipe, including original weighted faulty products and per-item recovery chances. */
public record PrecisionAssemblerRecipe(GenericRecipeData data) implements Recipe<RecipeInput>,GenericMachineRecipe {
 public static final MapCodec<PrecisionAssemblerRecipe>CODEC=GenericRecipeData.MAP_CODEC.xmap(PrecisionAssemblerRecipe::new,PrecisionAssemblerRecipe::data);
 @Override public java.util.List<net.minecraft.network.chat.Component> tooltip(){return GenericMachineRecipe.super.tooltip().stream().map(line->line.getString().equals("  One of:")?net.minecraft.network.chat.Component.translatable("gui.hbm.precass.one_of"):line).toList();}
 @Override public boolean matches(RecipeInput input,Level level){if(input.size()<inputItems().size())return false;for(int i=0;i<inputItems().size();i++)if(!inputItems().get(i).matches(input.getItem(i),false))return false;return true;}
 @Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().collapse(net.minecraft.util.RandomSource.create());}
 @Override public ItemStack getResultItem(HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().pool().getFirst().stack();}
 @Override public NonNullList<Ingredient>getIngredients(){var result=NonNullList.<Ingredient>create();inputItems().forEach(value->result.add(value.ingredient()));return result;}
 @Override public boolean canCraftInDimensions(int width,int height){return true;}@Override public boolean isSpecial(){return true;}
 @Override public RecipeType<?>getType(){return PrecisionAssemblerContent.TYPE.get();}@Override public RecipeSerializer<?>getSerializer(){return PrecisionAssemblerContent.SERIALIZER.get();}
 public static final class Serializer implements RecipeSerializer<PrecisionAssemblerRecipe>{@Override public MapCodec<PrecisionAssemblerRecipe>codec(){return CODEC;}@Override public StreamCodec<RegistryFriendlyByteBuf,PrecisionAssemblerRecipe>streamCodec(){return GenericRecipeData.STREAM_CODEC.map(PrecisionAssemblerRecipe::new,PrecisionAssemblerRecipe::data);}}
}
