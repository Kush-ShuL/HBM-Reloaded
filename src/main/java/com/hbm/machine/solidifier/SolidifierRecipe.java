package com.hbm.machine.solidifier;

import com.hbm.fluid.*;
import com.hbm.recipe.generic.GenericRecipeData;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/** One actual SolidificationRecipes fluid key, quantity and complete source output stack. */
public record SolidifierRecipe(FluidStack input,ItemStack output,int order) implements Recipe<RecipeInput> {
    public static final MapCodec<SolidifierRecipe> CODEC=RecordCodecBuilder.mapCodec(i->i.group(
            GenericRecipeData.FLUID_CODEC.fieldOf("input").forGetter(SolidifierRecipe::input),
            ItemStack.CODEC.fieldOf("output").forGetter(SolidifierRecipe::output),
            Codec.INT.optionalFieldOf("order",0).forGetter(SolidifierRecipe::order)).apply(i,SolidifierRecipe::new));
    @Override public boolean matches(RecipeInput in,Level level){return false;}
    @Override public ItemStack assemble(RecipeInput in,HolderLookup.Provider lookup){return output.copy();}
    @Override public ItemStack getResultItem(HolderLookup.Provider lookup){return output.copy();}
    @Override public boolean isSpecial(){return true;}
    @Override public boolean canCraftInDimensions(int width,int height){return true;}
    @Override public RecipeType<?> getType(){return SolidifierContent.TYPE.get();}
    @Override public RecipeSerializer<?> getSerializer(){return SolidifierContent.SERIALIZER.get();}
    public static final class Serializer implements RecipeSerializer<SolidifierRecipe>{
        @Override public MapCodec<SolidifierRecipe> codec(){return CODEC;}
        @Override public StreamCodec<RegistryFriendlyByteBuf,SolidifierRecipe> streamCodec(){return StreamCodec.of((buffer,recipe)->{
            buffer.writeUtf(recipe.input.type.getName());buffer.writeInt(recipe.input.fill);buffer.writeInt(recipe.input.pressure);
            ItemStack.STREAM_CODEC.encode(buffer,recipe.output);buffer.writeInt(recipe.order);
        },buffer->new SolidifierRecipe(new FluidStack(HbmFluids.fromName(buffer.readUtf()),buffer.readInt(),buffer.readInt()),ItemStack.STREAM_CODEC.decode(buffer),buffer.readInt()));}
    }
}
