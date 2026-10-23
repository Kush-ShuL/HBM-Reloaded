package com.hbm.machine.shredder;

import com.hbm.hazard.HbmHazards;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

/** Original ComparableStack matches item and damage/metadata, ignoring arbitrary NBT. */
public record ShredderRecipe(ItemStack input, int metadata, ItemStack result) implements Recipe<SingleRecipeInput> {
    public static final MapCodec<ShredderRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.STRICT_CODEC.fieldOf("input").forGetter(ShredderRecipe::input),
            com.mojang.serialization.Codec.INT.optionalFieldOf("metadata", 0).forGetter(ShredderRecipe::metadata),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(ShredderRecipe::result)).apply(instance, ShredderRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ShredderRecipe> STREAM_CODEC = StreamCodec.of((buffer, recipe) -> {
        ItemStack.STREAM_CODEC.encode(buffer, recipe.input); buffer.writeInt(recipe.metadata); ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
    }, buffer -> new ShredderRecipe(ItemStack.STREAM_CODEC.decode(buffer), buffer.readInt(), ItemStack.STREAM_CODEC.decode(buffer)));
    public static int metadata(ItemStack stack) { return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), stack.getDamageValue()); }
    @Override public boolean matches(SingleRecipeInput input, Level level) { return com.hbm.ore.OreRecipeInputs.sameLegacy(this.input, metadata, input.item()); }
    @Override public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) { return result.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return true; }
    @Override public ItemStack getResultItem(HolderLookup.Provider registries) { return result; }
    @Override public RecipeSerializer<?> getSerializer() { return ShredderContent.SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return ShredderContent.RECIPE_TYPE.get(); }
    @Override public boolean isSpecial() { return true; }
    @Override public ItemStack getToastSymbol() { return new ItemStack(ShredderContent.ITEM.get()); }
    @Override public NonNullList<Ingredient> getIngredients() { return NonNullList.of(Ingredient.EMPTY, Ingredient.of(input)); }
    public static final class Serializer implements RecipeSerializer<ShredderRecipe> {
        @Override public MapCodec<ShredderRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, ShredderRecipe> streamCodec() { return STREAM_CODEC; }
    }
}
