package com.hbm.item.meteorbook;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/** MagicRecipes compresses nonempty slots in row-major order, and deliberately does NOT sort them. */
public record MagicBookRecipe(int order, List<MagicBookIngredient> inputs, ItemStack result) implements Recipe<MagicBookInput> {
    public static final MapCodec<MagicBookRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.INT.fieldOf("order").forGetter(MagicBookRecipe::order),
        MagicBookIngredient.CODEC.listOf().fieldOf("inputs").forGetter(MagicBookRecipe::inputs),
        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(MagicBookRecipe::result)).apply(instance, MagicBookRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, MagicBookRecipe> STREAM_CODEC = StreamCodec.of((buffer, recipe) -> {
        buffer.writeVarInt(recipe.order); buffer.writeVarInt(recipe.inputs.size());
        recipe.inputs.forEach(ingredient -> ingredient.encode(buffer));
        ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
    }, buffer -> {
        int order = buffer.readVarInt(), count = buffer.readVarInt();
        if (count < 1 || count > 4) throw new IllegalArgumentException("Magic recipe must have one to four inputs");
        var inputs = new java.util.ArrayList<MagicBookIngredient>(count);
        for (int i = 0; i < count; i++) inputs.add(MagicBookIngredient.decode(buffer));
        return new MagicBookRecipe(order, List.copyOf(inputs), ItemStack.STREAM_CODEC.decode(buffer));
    });
    public MagicBookRecipe {
        if (inputs.isEmpty() || inputs.size() > 4) throw new IllegalArgumentException("Magic recipe must have one to four inputs");
        inputs = List.copyOf(inputs);
    }
    @Override public boolean matches(MagicBookInput input, Level level) {
        int next = 0;
        for (ItemStack stack : input.items()) if (!stack.isEmpty()) {
            if (next >= inputs.size() || !inputs.get(next++).matches(stack)) return false;
        }
        return next == inputs.size();
    }
    @Override public ItemStack assemble(MagicBookInput input, HolderLookup.Provider registries) { return result.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= inputs.size(); }
    @Override public ItemStack getResultItem(HolderLookup.Provider registries) { return result; }
    @Override public RecipeSerializer<?> getSerializer() { return MeteorBookContent.SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return MeteorBookContent.TYPE.get(); }
    @Override public boolean isSpecial() { return true; }
    @Override public ItemStack getToastSymbol() { return new ItemStack(MeteorBookContent.BOOK.get()); }
    @Override public NonNullList<Ingredient> getIngredients() { return NonNullList.of(Ingredient.EMPTY, inputs.stream().map(MagicBookIngredient::ingredient).toArray(Ingredient[]::new)); }
    public static final class Serializer implements RecipeSerializer<MagicBookRecipe> {
        @Override public MapCodec<MagicBookRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, MagicBookRecipe> streamCodec() { return STREAM_CODEC; }
    }
}
