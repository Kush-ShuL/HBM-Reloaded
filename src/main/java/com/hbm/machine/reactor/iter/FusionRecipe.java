package com.hbm.machine.reactor.iter;

import com.hbm.recipe.generic.*;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/** Original FusionRecipe's distinct KyU ignition / TU output / unshared neutron flux / RGB fields. */
public record FusionRecipe(GenericRecipeData data,long ignitionTemp,long outputTemp,double neutronFlux,float r,float g,float b) implements Recipe<RecipeInput>,GenericMachineRecipe {
    public static final MapCodec<FusionRecipe> CODEC=RecordCodecBuilder.mapCodec(i->i.group(GenericRecipeData.MAP_CODEC.forGetter(FusionRecipe::data),Codec.LONG.fieldOf("ignition_temp").forGetter(FusionRecipe::ignitionTemp),Codec.LONG.fieldOf("output_temp").forGetter(FusionRecipe::outputTemp),Codec.DOUBLE.fieldOf("neutron_flux").forGetter(FusionRecipe::neutronFlux),Codec.FLOAT.optionalFieldOf("r",1F).forGetter(FusionRecipe::r),Codec.FLOAT.optionalFieldOf("g",.2F).forGetter(FusionRecipe::g),Codec.FLOAT.optionalFieldOf("b",.6F).forGetter(FusionRecipe::b)).apply(i,FusionRecipe::new));
    @Override public boolean matches(RecipeInput input,Level level){return true;}@Override public boolean canCraftInDimensions(int width,int height){return true;}
    @Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().collapse(net.minecraft.util.RandomSource.create());}
    @Override public ItemStack getResultItem(HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().pool().getFirst().stack();}
    @Override public boolean isSpecial(){return true;}@Override public RecipeType<?> getType(){return IterRecipes.TYPE.get();}@Override public RecipeSerializer<?> getSerializer(){return IterRecipes.SERIALIZER.get();}
    @Override public List<net.minecraft.network.chat.Component> tooltip(){var lines=new ArrayList<>(GenericMachineRecipe.super.tooltip());lines.add(3,net.minecraft.network.chat.Component.translatable("gui.recipe.fusionIn").append(": "+com.hbm.item.battery.BatteryItem.shortNumber(ignitionTemp)+"KyU/t"));lines.add(4,net.minecraft.network.chat.Component.translatable("gui.recipe.fusionOut").append(": "+com.hbm.item.battery.BatteryItem.shortNumber(outputTemp)+"TU/t"));lines.add(5,net.minecraft.network.chat.Component.translatable("gui.recipe.fusionFlux").append(": "+((int)(neutronFlux*10))/10D+" flux/t"));return lines;}
    public static final class Serializer implements RecipeSerializer<FusionRecipe>{public MapCodec<FusionRecipe> codec(){return CODEC;}public StreamCodec<RegistryFriendlyByteBuf,FusionRecipe> streamCodec(){return StreamCodec.of((buf,recipe)->{recipe.data.encode(buf);buf.writeLong(recipe.ignitionTemp);buf.writeLong(recipe.outputTemp);buf.writeDouble(recipe.neutronFlux);buf.writeFloat(recipe.r);buf.writeFloat(recipe.g);buf.writeFloat(recipe.b);},buf->new FusionRecipe(GenericRecipeData.decode(buf),buf.readLong(),buf.readLong(),buf.readDouble(),buf.readFloat(),buf.readFloat(),buf.readFloat()));}}
}
