package com.hbm.machine.centrifuge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/** Original positional outputs: empty result slots are retained, never compacted. */
public record CentrifugeRecipe(Ingredient input,List<ItemStack> results,List<ItemStack> lbsmResults,boolean exactInput) implements Recipe<SingleRecipeInput> {
    private static final Codec<List<ItemStack>> OUTPUTS=ItemStack.OPTIONAL_CODEC.listOf();
    public static final MapCodec<CentrifugeRecipe> CODEC=RecordCodecBuilder.mapCodec(instance->instance.group(
        Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(CentrifugeRecipe::input),
        OUTPUTS.fieldOf("results").forGetter(CentrifugeRecipe::results),
        OUTPUTS.optionalFieldOf("lbsm_results",List.of()).forGetter(CentrifugeRecipe::lbsmResults),
        Codec.BOOL.optionalFieldOf("exact_input",false).forGetter(CentrifugeRecipe::exactInput)).apply(instance,CentrifugeRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf,CentrifugeRecipe> STREAM_CODEC=StreamCodec.of((buffer,recipe)-> {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer,recipe.input); buffer.writeVarInt(recipe.outputs().size()); for(var stack:recipe.outputs()) ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer,stack);
        // The server's configured branch is authoritative; client config cannot alter displayed outputs.
        buffer.writeVarInt(0); buffer.writeBoolean(recipe.exactInput);
    },buffer->{ Ingredient input=Ingredient.CONTENTS_STREAM_CODEC.decode(buffer); int size=buffer.readVarInt(); var results=new java.util.ArrayList<ItemStack>(); for(int i=0;i<size;i++) results.add(ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer)); size=buffer.readVarInt(); var lbs=new java.util.ArrayList<ItemStack>(); for(int i=0;i<size;i++) lbs.add(ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer)); return new CentrifugeRecipe(input,results,lbs,buffer.readBoolean()); });
    public CentrifugeRecipe { results=List.copyOf(results); lbsmResults=List.copyOf(lbsmResults); }
    public List<ItemStack> outputs() { return CentrifugeConfig.lbs()&&!lbsmResults.isEmpty()?lbsmResults:results; }
    @Override public boolean matches(SingleRecipeInput input,Level level) { return com.hbm.ore.OreRecipeInputs.test(this.input,input.item()); }
    @Override public ItemStack assemble(SingleRecipeInput input,HolderLookup.Provider lookup) { return getResultItem(lookup).copy(); }
    @Override public ItemStack getResultItem(HolderLookup.Provider lookup) { return outputs().stream().filter(stack->!stack.isEmpty()).findFirst().orElse(ItemStack.EMPTY); }
    @Override public boolean canCraftInDimensions(int width,int height) { return true; }
    @Override public boolean isSpecial() { return true; }
    @Override public NonNullList<Ingredient> getIngredients() { return NonNullList.of(Ingredient.EMPTY,input); }
    @Override public RecipeType<?> getType() { return CentrifugeContent.TYPE.get(); }
    @Override public RecipeSerializer<?> getSerializer() { return CentrifugeContent.SERIALIZER.get(); }
    @Override public ItemStack getToastSymbol() { return new ItemStack(CentrifugeContent.ITEM.get()); }
    public static final class Serializer implements RecipeSerializer<CentrifugeRecipe> {
        @Override public MapCodec<CentrifugeRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf,CentrifugeRecipe> streamCodec() { return STREAM_CODEC; }
    }
}
