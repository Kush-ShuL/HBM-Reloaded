package com.hbm.machine.alloy;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
/** Original legacy blast recipes match both input orders and consume exactly one of each. */
public record AlloyRecipe(Ingredient first,Ingredient second,ItemStack result,boolean hidden,int order) implements Recipe<AlloyInput> {
    public AlloyRecipe(Ingredient first,Ingredient second,ItemStack result,boolean hidden) { this(first,second,result,hidden,0); }
    public static final MapCodec<AlloyRecipe> CODEC=RecordCodecBuilder.mapCodec(instance->instance.group(Ingredient.CODEC_NONEMPTY.fieldOf("first").forGetter(AlloyRecipe::first),Ingredient.CODEC_NONEMPTY.fieldOf("second").forGetter(AlloyRecipe::second),ItemStack.CODEC.fieldOf("result").forGetter(AlloyRecipe::result),Codec.BOOL.optionalFieldOf("hidden",false).forGetter(AlloyRecipe::hidden),Codec.INT.optionalFieldOf("order",0).forGetter(AlloyRecipe::order)).apply(instance,AlloyRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf,AlloyRecipe> STREAM_CODEC=StreamCodec.of((buf,recipe)->{ Ingredient.CONTENTS_STREAM_CODEC.encode(buf,recipe.first); Ingredient.CONTENTS_STREAM_CODEC.encode(buf,recipe.second); ItemStack.STREAM_CODEC.encode(buf,recipe.result); buf.writeBoolean(recipe.hidden); buf.writeInt(recipe.order); },buf->new AlloyRecipe(Ingredient.CONTENTS_STREAM_CODEC.decode(buf),Ingredient.CONTENTS_STREAM_CODEC.decode(buf),ItemStack.STREAM_CODEC.decode(buf),buf.readBoolean(),buf.readInt()));
    @Override public boolean matches(AlloyInput input,Level level) { return com.hbm.ore.OreRecipeInputs.test(first,input.upper())&&com.hbm.ore.OreRecipeInputs.test(second,input.lower())||com.hbm.ore.OreRecipeInputs.test(first,input.lower())&&com.hbm.ore.OreRecipeInputs.test(second,input.upper()); }
    @Override public ItemStack assemble(AlloyInput input,HolderLookup.Provider lookup) { return result.copy(); }
    @Override public ItemStack getResultItem(HolderLookup.Provider lookup) { return result; }
    @Override public NonNullList<Ingredient> getIngredients() { return NonNullList.of(Ingredient.EMPTY,first,second); }
    @Override public boolean isSpecial() { return true; }
    @Override public boolean canCraftInDimensions(int x,int y) { return true; }
    @Override public RecipeType<?> getType() { return AlloyContent.TYPE.get(); }
    @Override public RecipeSerializer<?> getSerializer() { return AlloyContent.SERIALIZER.get(); }
    @Override public ItemStack getToastSymbol() { return new ItemStack(AlloyContent.ITEM.get()); }
    public static final class Serializer implements RecipeSerializer<AlloyRecipe> {
        @Override public MapCodec<AlloyRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf,AlloyRecipe> streamCodec() { return STREAM_CODEC; }
    }
}
