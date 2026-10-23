package com.hbm.recipe.generic;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
public record GenericIngredient(Ingredient ingredient,int count) {
    public static final Codec<GenericIngredient> CODEC=RecordCodecBuilder.create(i->i.group(Ingredient.CODEC.fieldOf("ingredient").forGetter(GenericIngredient::ingredient),Codec.INT.optionalFieldOf("count",1).forGetter(GenericIngredient::count)).apply(i,GenericIngredient::new));
    public boolean matches(ItemStack stack,boolean ignoreCount){return !stack.isEmpty()&&ingredient.test(stack)&&(ignoreCount||stack.getCount()>=count);}
    /** Explicit metal extraction only; ordinary crafting continues to use matches/examples. */
    public boolean matchesProcessing(ItemStack stack,boolean ignoreCount){return !stack.isEmpty()&&com.hbm.ore.OreRecipeInputs.test(ingredient,stack)&&(ignoreCount||stack.getCount()>=count);}
    public java.util.List<ItemStack> processingExamples(){return com.hbm.ore.OreRecipeInputs.examples(ingredient,Math.max(1,count));}
    public java.util.List<ItemStack> examples(){return java.util.Arrays.stream(ingredient.getItems()).map(s->s.copyWithCount(Math.max(1,count))).toList();}
    public void encode(RegistryFriendlyByteBuf b){Ingredient.CONTENTS_STREAM_CODEC.encode(b,ingredient);b.writeInt(count);}
    public static GenericIngredient decode(RegistryFriendlyByteBuf b){return new GenericIngredient(Ingredient.CONTENTS_STREAM_CODEC.decode(b),b.readInt());}
}
