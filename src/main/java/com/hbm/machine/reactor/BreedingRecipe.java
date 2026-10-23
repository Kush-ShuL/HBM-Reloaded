package com.hbm.machine.reactor;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
/** Original ComparableStack matching and per-recipe minimum neutron flux. */
public record BreedingRecipe(Ingredient input,ItemStack result,int flux,int order) implements Recipe<SingleRecipeInput> {
    public static final MapCodec<BreedingRecipe> CODEC=RecordCodecBuilder.mapCodec(instance->instance.group(Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(BreedingRecipe::input),ItemStack.CODEC.fieldOf("result").forGetter(BreedingRecipe::result),Codec.INT.fieldOf("flux").forGetter(BreedingRecipe::flux),Codec.INT.optionalFieldOf("order",0).forGetter(BreedingRecipe::order)).apply(instance,BreedingRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf,BreedingRecipe> STREAM_CODEC=StreamCodec.of((buffer,recipe)->{Ingredient.CONTENTS_STREAM_CODEC.encode(buffer,recipe.input);ItemStack.STREAM_CODEC.encode(buffer,recipe.result);buffer.writeInt(recipe.flux);buffer.writeInt(recipe.order);},buffer->new BreedingRecipe(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer),ItemStack.STREAM_CODEC.decode(buffer),buffer.readInt(),buffer.readInt()));
    @Override public boolean matches(SingleRecipeInput input,Level level){return this.input.test(input.item());}
    @Override public ItemStack assemble(SingleRecipeInput input,HolderLookup.Provider lookup){return result.copy();}
    @Override public ItemStack getResultItem(HolderLookup.Provider lookup){return result;}
    @Override public NonNullList<Ingredient> getIngredients(){return NonNullList.of(Ingredient.EMPTY,input);}
    @Override public boolean isSpecial(){return true;}@Override public boolean canCraftInDimensions(int x,int y){return true;}
    @Override public RecipeType<?> getType(){return ReactorContent.BREEDING_TYPE.get();}@Override public RecipeSerializer<?> getSerializer(){return ReactorContent.BREEDING_SERIALIZER.get();}
    public static final class Serializer implements RecipeSerializer<BreedingRecipe>{@Override public MapCodec<BreedingRecipe> codec(){return CODEC;}@Override public StreamCodec<RegistryFriendlyByteBuf,BreedingRecipe> streamCodec(){return STREAM_CODEC;}}
}
