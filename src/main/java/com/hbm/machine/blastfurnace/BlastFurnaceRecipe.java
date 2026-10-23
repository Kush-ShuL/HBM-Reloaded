package com.hbm.machine.blastfurnace;

import com.hbm.recipe.generic.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/** Original GenericRecipeNoPower, native synchronized and matched without ordering its two inputs. */
public record BlastFurnaceRecipe(GenericRecipeData data) implements Recipe<RecipeInput>,GenericMachineRecipe {
    public static final MapCodec<BlastFurnaceRecipe> CODEC=GenericRecipeData.MAP_CODEC.xmap(BlastFurnaceRecipe::new,BlastFurnaceRecipe::data);
    @Override public boolean matches(RecipeInput input,Level level){return input.size()>=2&&accepts(input.getItem(0),input.getItem(1));}
    /** The source lookup intentionally ignores count on its first lane only. */
    public boolean accepts(ItemStack first,ItemStack second){
        if(inputItems().size()==1)return second.isEmpty()&&inputItems().getFirst().matchesProcessing(first,false)||first.isEmpty()&&inputItems().getFirst().matchesProcessing(second,false);
        if(inputItems().size()!=2||first.isEmpty()||second.isEmpty())return false;
        return inputItems().get(0).matchesProcessing(first,true)&&inputItems().get(1).matchesProcessing(second,false)||inputItems().get(1).matchesProcessing(first,true)&&inputItems().get(0).matchesProcessing(second,false);
    }
    public int[] consumption(ItemStack first,ItemStack second){
        if(inputItems().size()==1){var in=inputItems().getFirst();return in.matchesProcessing(first,false)?new int[]{in.count(),0}:in.matchesProcessing(second,false)?new int[]{0,in.count()}:null;}
        if(inputItems().size()==2){var a=inputItems().get(0);var b=inputItems().get(1);if(a.matchesProcessing(first,false)&&b.matchesProcessing(second,false))return new int[]{a.count(),b.count()};if(a.matchesProcessing(second,false)&&b.matchesProcessing(first,false))return new int[]{b.count(),a.count()};}
        return null;
    }
    @Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().collapse(net.minecraft.util.RandomSource.create());}
    @Override public ItemStack getResultItem(HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().pool().getFirst().stack();}
    @Override public boolean canCraftInDimensions(int width,int height){return true;}@Override public boolean isSpecial(){return true;}
    @Override public NonNullList<Ingredient> getIngredients(){var ingredients=NonNullList.<Ingredient>create();inputItems().forEach(input->ingredients.add(input.ingredient()));return ingredients;}
    @Override public RecipeType<?> getType(){return BlastFurnaceContent.TYPE.get();}@Override public RecipeSerializer<?> getSerializer(){return BlastFurnaceContent.SERIALIZER.get();}
    public static final class Serializer implements RecipeSerializer<BlastFurnaceRecipe>{
        @Override public MapCodec<BlastFurnaceRecipe> codec(){return CODEC;}
        @Override public StreamCodec<RegistryFriendlyByteBuf,BlastFurnaceRecipe> streamCodec(){return GenericRecipeData.STREAM_CODEC.map(BlastFurnaceRecipe::new,BlastFurnaceRecipe::data);}
    }
}
