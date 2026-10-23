package com.hbm.item.meteorbook;

import com.hbm.hazard.HbmHazards;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/** ComparableStack keeps exact old damage/metadata, ignores unrelated NBT, and counts each occupied slot once. */
public record MagicBookIngredient(Ingredient ingredient, int metadata) {
    public static final Codec<MagicBookIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(MagicBookIngredient::ingredient),
        Codec.INT.optionalFieldOf("metadata", 0).forGetter(MagicBookIngredient::metadata)).apply(instance, MagicBookIngredient::new));
    public boolean matches(ItemStack stack) {
        return ingredient.test(stack) && (metadata == 32767 || metadata == stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), stack.getDamageValue()));
    }
    public List<ItemStack> examples() {
        return Arrays.stream(ingredient.getItems()).map(value -> {
            ItemStack stack = value.copy();
            if (metadata != 32767) {
                if (stack.isDamageableItem()) stack.setDamageValue(metadata);
                else if (metadata != 0 || stack.has(HbmHazards.LEGACY_METADATA.get())) stack.set(HbmHazards.LEGACY_METADATA.get(), metadata);
            }
            return stack;
        }).toList();
    }
    public void encode(RegistryFriendlyByteBuf buffer) { Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient); buffer.writeVarInt(metadata); }
    public static MagicBookIngredient decode(RegistryFriendlyByteBuf buffer) { return new MagicBookIngredient(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), buffer.readVarInt()); }
}
