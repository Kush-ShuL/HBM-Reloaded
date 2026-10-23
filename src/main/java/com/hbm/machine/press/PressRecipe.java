package com.hbm.machine.press;

import com.hbm.item.StampItem;
import com.hbm.item.StampItem.StampType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/** One source material, one reusable stamp, source output/count/components. */
public record PressRecipe(Ingredient input, StampType stamp, ItemStack result) implements Recipe<PressInput> {
    private static final Codec<StampType> STAMP_CODEC = Codec.STRING.xmap(s -> StampType.valueOf(s.toUpperCase(Locale.ROOT)), s -> s.name().toLowerCase(Locale.ROOT));
    public static final MapCodec<PressRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(PressRecipe::input),
            STAMP_CODEC.fieldOf("stamp").forGetter(PressRecipe::stamp),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(PressRecipe::result)).apply(instance, PressRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, PressRecipe> STREAM_CODEC = StreamCodec.of((buffer, recipe) -> {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);
        buffer.writeEnum(recipe.stamp);
        ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
    }, buffer -> new PressRecipe(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), buffer.readEnum(StampType.class), ItemStack.STREAM_CODEC.decode(buffer)));
    @Override public boolean matches(PressInput input, Level level) { return input.stamp().getItem() instanceof StampItem tool && tool.stampType(input.stamp()) == stamp && this.input.test(input.material()); }
    @Override public ItemStack assemble(PressInput input, HolderLookup.Provider registries) { return result.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return true; }
    @Override public ItemStack getResultItem(HolderLookup.Provider registries) { return result; }
    @Override public RecipeSerializer<?> getSerializer() { return PressContent.SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return PressContent.RECIPE_TYPE.get(); }
    @Override public boolean isSpecial() { return true; }
    @Override public ItemStack getToastSymbol() { return new ItemStack(PressContent.ITEM.get()); }
    @Override public NonNullList<Ingredient> getIngredients() { return NonNullList.of(Ingredient.EMPTY, input, Ingredient.of(StampItem.stamps(stamp).stream())); }
    public static final class Serializer implements RecipeSerializer<PressRecipe> {
        @Override public MapCodec<PressRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, PressRecipe> streamCodec() { return STREAM_CODEC; }
    }
}
