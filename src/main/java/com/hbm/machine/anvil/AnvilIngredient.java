package com.hbm.machine.anvil;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
/** Count belongs to the original AStack, separate from its item/metadata/ore matching. */
public record AnvilIngredient(Ingredient ingredient,int count) {
    public static final Codec<AnvilIngredient> CODEC=RecordCodecBuilder.create(instance->instance.group(Ingredient.CODEC.fieldOf("ingredient").forGetter(AnvilIngredient::ingredient),Codec.intRange(0,Integer.MAX_VALUE).optionalFieldOf("count",1).forGetter(AnvilIngredient::count)).apply(instance,AnvilIngredient::new));
    public boolean matches(ItemStack stack,boolean ignoreCount) { return !stack.isEmpty()&&ingredient.test(stack)&&(ignoreCount||stack.getCount()>=count); }
    public void encode(RegistryFriendlyByteBuf buffer) { Ingredient.CONTENTS_STREAM_CODEC.encode(buffer,ingredient); buffer.writeVarInt(count); }
    public static AnvilIngredient decode(RegistryFriendlyByteBuf buffer) { return new AnvilIngredient(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer),buffer.readVarInt()); }
    public java.util.List<ItemStack> examples() { return java.util.Arrays.stream(ingredient.getItems()).map(stack->stack.copyWithCount(Math.max(1,count))).toList(); }
}
