package com.hbm.machine.alloy;
import com.hbm.hazard.HbmHazards;
import com.mojang.serialization.MapCodec;
import java.util.stream.Stream;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.*;
/** ComparableStack equality: item + legacy damage, ignoring unrelated NBT and modern components. */
public record AlloyStackIngredient(ItemStack stack) implements ICustomIngredient {
    public static final MapCodec<AlloyStackIngredient> CODEC=ItemStack.CODEC.fieldOf("stack").xmap(AlloyStackIngredient::new,AlloyStackIngredient::stack);
    @Override public boolean test(ItemStack candidate) { return !candidate.isEmpty()&&candidate.getItem()==stack.getItem()&&(metadata(stack)==32767||metadata(stack)==metadata(candidate)); }
    private static int metadata(ItemStack value) { return value.getOrDefault(HbmHazards.LEGACY_METADATA.get(),value.getDamageValue()); }
    @Override public Stream<ItemStack> getItems() { return Stream.of(stack.copyWithCount(1)); }
    @Override public boolean isSimple() { return false; }
    @Override public IngredientType<?> getType() { return AlloyContent.STACK_INGREDIENT.get(); }
}
