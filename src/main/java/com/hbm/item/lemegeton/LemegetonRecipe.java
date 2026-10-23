package com.hbm.item.lemegeton;

import com.hbm.hazard.HbmHazards;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/** Exact original ignoreSize recipe match; ComparableStack metadata and OreDictStack wildcard remain distinct. */
public record LemegetonRecipe(int order, String sourceKey, int sourceHash, Ingredient ingredient, int metadata, ItemStack result) implements Recipe<LemegetonInput> {
    public static final MapCodec<LemegetonRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.INT.fieldOf("source_order").forGetter(LemegetonRecipe::order),
        Codec.STRING.fieldOf("source_key").forGetter(LemegetonRecipe::sourceKey),
        Codec.INT.fieldOf("source_hash").forGetter(LemegetonRecipe::sourceHash),
        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(LemegetonRecipe::ingredient),
        Codec.INT.optionalFieldOf("metadata", 32767).forGetter(LemegetonRecipe::metadata),
        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(LemegetonRecipe::result)).apply(instance, LemegetonRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, LemegetonRecipe> STREAM_CODEC = StreamCodec.of((buffer, recipe) -> {
        buffer.writeVarInt(recipe.order); buffer.writeUtf(recipe.sourceKey); buffer.writeInt(recipe.sourceHash);
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.ingredient); buffer.writeVarInt(recipe.metadata); ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
    }, buffer -> new LemegetonRecipe(buffer.readVarInt(), buffer.readUtf(), buffer.readInt(), Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), buffer.readVarInt(), ItemStack.STREAM_CODEC.decode(buffer)));
    @Override public boolean matches(LemegetonInput input, Level level) { var stack = input.item(); return !stack.isEmpty() && ingredient.test(stack) && (metadata == 32767 || metadata == stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), stack.getDamageValue())); }
    @Override public ItemStack assemble(LemegetonInput input, HolderLookup.Provider registries) { return result.copy(); }
    @Override public ItemStack getResultItem(HolderLookup.Provider registries) { return result; }
    @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 1; }
    @Override public RecipeType<?> getType() { return LemegetonContent.TYPE.get(); }
    @Override public RecipeSerializer<?> getSerializer() { return LemegetonContent.SERIALIZER.get(); }
    @Override public boolean isSpecial() { return true; }
    @Override public ItemStack getToastSymbol() { return new ItemStack(LemegetonContent.BOOK.get()); }
    @Override public NonNullList<Ingredient> getIngredients() { return NonNullList.of(Ingredient.EMPTY, ingredient); }
    public List<ItemStack> examples() { return java.util.Arrays.stream(ingredient.getItems()).map(stack -> { var copy = stack.copy(); if (metadata != 32767) { if (copy.isDamageableItem()) copy.setDamageValue(metadata); else if (metadata != 0 || copy.has(HbmHazards.LEGACY_METADATA.get())) copy.set(HbmHazards.LEGACY_METADATA.get(), metadata); } return copy; }).toList(); }
    public static final class Serializer implements RecipeSerializer<LemegetonRecipe> {
        @Override public MapCodec<LemegetonRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, LemegetonRecipe> streamCodec() { return STREAM_CODEC; }
    }
}
