package com.hbm.machine.rockmill;
import com.hbm.recipe.generic.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
/** Original positional inputs and one weighted choice per output lane; native recipe synchronization. */
public record RockMillRecipe(GenericRecipeData data) implements Recipe<RecipeInput>,GenericMachineRecipe {
 public static final MapCodec<RockMillRecipe> CODEC=GenericRecipeData.MAP_CODEC.xmap(RockMillRecipe::new,RockMillRecipe::data);
 @Override public boolean matches(RecipeInput input,Level level){if(input.size()<inputItems().size())return false;for(int i=0;i<inputItems().size();i++)if(!inputItems().get(i).matchesProcessing(input.getItem(i),false))return false;return true;}
 @Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().collapse(net.minecraft.util.RandomSource.create());}
 @Override public ItemStack getResultItem(HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().pool().getFirst().stack();}
 @Override public boolean canCraftInDimensions(int width,int height){return true;}@Override public boolean isSpecial(){return true;}
 @Override public NonNullList<Ingredient> getIngredients(){var result=NonNullList.<Ingredient>create();inputItems().forEach(in->result.add(in.ingredient()));return result;}
 @Override public RecipeType<?> getType(){return RockMillContent.TYPE.get();}@Override public RecipeSerializer<?> getSerializer(){return RockMillContent.SERIALIZER.get();}
 public static final class Serializer implements RecipeSerializer<RockMillRecipe>{@Override public MapCodec<RockMillRecipe> codec(){return CODEC;}@Override public StreamCodec<RegistryFriendlyByteBuf,RockMillRecipe> streamCodec(){return GenericRecipeData.STREAM_CODEC.map(RockMillRecipe::new,RockMillRecipe::data);}}
}
