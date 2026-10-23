package com.hbm.machine.oilliquefactor;
import com.hbm.fluid.*;
import com.hbm.recipe.generic.*;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
/** Source ComparableStack/dictionary key is retained separately, so exact always precedes wildcard. */
public record LiquefactorRecipe(GenericIngredient input,FluidStack output,String keyKind,String sourceKey,String legacyInput,int order) implements Recipe<RecipeInput> {
 public static final MapCodec<LiquefactorRecipe> CODEC=RecordCodecBuilder.mapCodec(i->i.group(GenericIngredient.CODEC.fieldOf("input").forGetter(LiquefactorRecipe::input),GenericRecipeData.FLUID_CODEC.fieldOf("output").forGetter(LiquefactorRecipe::output),Codec.STRING.fieldOf("key_kind").forGetter(LiquefactorRecipe::keyKind),Codec.STRING.fieldOf("source_key").forGetter(LiquefactorRecipe::sourceKey),Codec.STRING.fieldOf("legacy_input").forGetter(LiquefactorRecipe::legacyInput),Codec.INT.fieldOf("order").forGetter(LiquefactorRecipe::order)).apply(i,LiquefactorRecipe::new));
 @Override public boolean matches(RecipeInput in,Level level){return !keyKind.equals("nbt")&&in.size()>0&&input.matches(in.getItem(0),true);}@Override public ItemStack assemble(RecipeInput in,HolderLookup.Provider lookup){return ItemStack.EMPTY;}@Override public ItemStack getResultItem(HolderLookup.Provider lookup){return ItemStack.EMPTY;}@Override public boolean isSpecial(){return true;}@Override public boolean canCraftInDimensions(int w,int h){return true;}@Override public RecipeType<?> getType(){return LiquefactorContent.TYPE.get();}@Override public RecipeSerializer<?> getSerializer(){return LiquefactorContent.SERIALIZER.get();}
 public static final class Serializer implements RecipeSerializer<LiquefactorRecipe> {@Override public MapCodec<LiquefactorRecipe> codec(){return CODEC;}@Override public StreamCodec<RegistryFriendlyByteBuf,LiquefactorRecipe> streamCodec(){return StreamCodec.of((b,r)->{r.input.encode(b);b.writeUtf(r.output.type.getName());b.writeInt(r.output.fill);b.writeInt(r.output.pressure);b.writeUtf(r.keyKind);b.writeUtf(r.sourceKey);b.writeUtf(r.legacyInput);b.writeInt(r.order);},b->new LiquefactorRecipe(GenericIngredient.decode(b),new FluidStack(HbmFluids.fromName(b.readUtf()),b.readInt(),b.readInt()),b.readUtf(),b.readUtf(),b.readUtf(),b.readInt()));}}
}
