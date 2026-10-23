package com.hbm.machine.reactor.iter.breeder;
import com.hbm.fluid.FluidStack;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.recipe.generic.GenericRecipeData;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
/** Actual type-keyed FluidBreederRecipes input/output, independent of the shared solid irradiation table. */
public record FluidBreedingRecipe(FluidStack input,FluidStack output,int order) implements Recipe<RecipeInput> {
 public static final MapCodec<FluidBreedingRecipe> CODEC=RecordCodecBuilder.mapCodec(i->i.group(GenericRecipeData.FLUID_CODEC.fieldOf("input").forGetter(FluidBreedingRecipe::input),GenericRecipeData.FLUID_CODEC.fieldOf("output").forGetter(FluidBreedingRecipe::output),Codec.INT.optionalFieldOf("order",0).forGetter(FluidBreedingRecipe::order)).apply(i,FluidBreedingRecipe::new));
 @Override public boolean matches(RecipeInput in,Level level){return true;}@Override public boolean canCraftInDimensions(int width,int height){return true;}
 @Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return FluidIconItem.make(output);}@Override public ItemStack getResultItem(HolderLookup.Provider lookup){return FluidIconItem.make(output);}@Override public boolean isSpecial(){return true;}@Override public RecipeType<?> getType(){return FusionBreederContent.FLUID_TYPE.get();}@Override public RecipeSerializer<?> getSerializer(){return FusionBreederContent.FLUID_SERIALIZER.get();}
 public static final class Serializer implements RecipeSerializer<FluidBreedingRecipe>{public MapCodec<FluidBreedingRecipe> codec(){return CODEC;}public StreamCodec<RegistryFriendlyByteBuf,FluidBreedingRecipe> streamCodec(){return StreamCodec.of((buf,r)->{GenericRecipeData.writeFluids(buf,java.util.List.of(r.input,r.output));buf.writeInt(r.order);},buf->{var values=GenericRecipeData.readFluids(buf);return new FluidBreedingRecipe(values.getFirst(),values.get(1),buf.readInt());});}}
}
