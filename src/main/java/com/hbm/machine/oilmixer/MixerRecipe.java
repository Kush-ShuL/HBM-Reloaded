package com.hbm.machine.oilmixer;
import com.hbm.fluid.FluidStack;
import com.hbm.recipe.generic.*;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
/** Native synchronized original output-selected variants; the mask retains input2-only configuration. */
public record MixerRecipe(GenericRecipeData data,int inputMask) implements Recipe<RecipeInput>,GenericMachineRecipe {
 public static final MapCodec<MixerRecipe> CODEC=RecordCodecBuilder.mapCodec(i->i.group(GenericRecipeData.MAP_CODEC.forGetter(MixerRecipe::data),Codec.INT.fieldOf("input_mask").forGetter(MixerRecipe::inputMask)).apply(i,MixerRecipe::new));
 public FluidStack fluid(int slot){if((inputMask&(1<<slot))==0)return null;return inputFluids().get(slot==1&&(inputMask&1)!=0?1:0);}
 @Override public boolean matches(RecipeInput in,Level level){return false;}@Override public ItemStack assemble(RecipeInput in,HolderLookup.Provider lookup){return ItemStack.EMPTY;}@Override public ItemStack getResultItem(HolderLookup.Provider lookup){return ItemStack.EMPTY;}@Override public boolean canCraftInDimensions(int w,int h){return true;}@Override public boolean isSpecial(){return true;}@Override public RecipeType<?> getType(){return MixerContent.TYPE.get();}@Override public RecipeSerializer<?> getSerializer(){return MixerContent.SERIALIZER.get();}
 public static final class Serializer implements RecipeSerializer<MixerRecipe>{@Override public MapCodec<MixerRecipe> codec(){return CODEC;}@Override public StreamCodec<RegistryFriendlyByteBuf,MixerRecipe> streamCodec(){return StreamCodec.of((b,r)->{r.data.encode(b);b.writeByte(r.inputMask);},b->new MixerRecipe(GenericRecipeData.decode(b),b.readUnsignedByte()));}}
}
