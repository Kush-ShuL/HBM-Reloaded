package com.hbm.machine.reactor.fuelpool;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/** Original ComparableStack identity+metadata matching; no fixed processing duration is invented. */
public record FuelPoolRecipe(Ingredient input,ItemStack result,int order) implements Recipe<SingleRecipeInput> {
    public static final MapCodec<FuelPoolRecipe> CODEC=RecordCodecBuilder.mapCodec(instance->instance.group(Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(FuelPoolRecipe::input),ItemStack.CODEC.fieldOf("result").forGetter(FuelPoolRecipe::result),Codec.INT.optionalFieldOf("order",0).forGetter(FuelPoolRecipe::order)).apply(instance,FuelPoolRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf,FuelPoolRecipe> STREAM_CODEC=StreamCodec.of((buffer,recipe)->{Ingredient.CONTENTS_STREAM_CODEC.encode(buffer,recipe.input);ItemStack.STREAM_CODEC.encode(buffer,recipe.result);buffer.writeInt(recipe.order);},buffer->new FuelPoolRecipe(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer),ItemStack.STREAM_CODEC.decode(buffer),buffer.readInt()));
    @Override public boolean matches(SingleRecipeInput input,Level level){return this.input.test(input.item());}@Override public ItemStack assemble(SingleRecipeInput input,HolderLookup.Provider lookup){return result.copy();}@Override public ItemStack getResultItem(HolderLookup.Provider lookup){return result;}@Override public NonNullList<Ingredient> getIngredients(){return NonNullList.of(Ingredient.EMPTY,input);}@Override public boolean isSpecial(){return true;}@Override public boolean canCraftInDimensions(int x,int y){return true;}@Override public RecipeType<?> getType(){return FuelPoolContent.TYPE.get();}@Override public RecipeSerializer<?> getSerializer(){return FuelPoolContent.SERIALIZER.get();}
    public static final class Serializer implements RecipeSerializer<FuelPoolRecipe>{@Override public MapCodec<FuelPoolRecipe> codec(){return CODEC;}@Override public StreamCodec<RegistryFriendlyByteBuf,FuelPoolRecipe> streamCodec(){return STREAM_CODEC;}}
}
