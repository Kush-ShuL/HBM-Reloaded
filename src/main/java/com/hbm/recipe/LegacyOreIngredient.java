package com.hbm.recipe;

import com.hbm.registry.HbmOreDictionary;
import com.mojang.serialization.MapCodec;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

/** Native old ore memberships include dynamic and metadata-specific containers. */
public record LegacyOreIngredient(ResourceLocation tag) implements ICustomIngredient {
    public static final MapCodec<LegacyOreIngredient> CODEC = ResourceLocation.CODEC.fieldOf("tag")
            .xmap(LegacyOreIngredient::new, LegacyOreIngredient::tag);

    @Override public boolean test(ItemStack stack) { return !stack.isEmpty() && HbmOreDictionary.tags(stack).contains(tag.toString()); }
    @Override public Stream<ItemStack> getItems() { return HbmOreDictionary.items(tag.toString()); }
    @Override public boolean isSimple() { return false; }
    @Override public IngredientType<?> getType() { return HbmOreDictionary.INGREDIENT.get(); }
}
