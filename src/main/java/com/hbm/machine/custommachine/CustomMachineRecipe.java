package com.hbm.machine.custommachine;

import com.hbm.recipe.generic.*;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/** Original named recipe-set entry. Matching/process order stays in the actual CustomMachineEntity. */
public record CustomMachineRecipe(String recipeKey,GenericRecipeData data,String pollutionType,float pollutionAmount,float radiationAmount,int flux,int heat) implements Recipe<RecipeInput>,GenericMachineRecipe {
    public static final MapCodec<CustomMachineRecipe> CODEC=RecordCodecBuilder.mapCodec(i->i.group(Codec.STRING.fieldOf("recipeKey").forGetter(CustomMachineRecipe::recipeKey),GenericRecipeData.CODEC.fieldOf("data").forGetter(CustomMachineRecipe::data),Codec.STRING.optionalFieldOf("pollutionType","").forGetter(CustomMachineRecipe::pollutionType),Codec.FLOAT.optionalFieldOf("pollutionAmount",0F).forGetter(CustomMachineRecipe::pollutionAmount),Codec.FLOAT.optionalFieldOf("radiationAmount",0F).forGetter(CustomMachineRecipe::radiationAmount),Codec.INT.optionalFieldOf("flux",0).forGetter(CustomMachineRecipe::flux),Codec.INT.optionalFieldOf("heat",0).forGetter(CustomMachineRecipe::heat)).apply(i,CustomMachineRecipe::new));
    @Override public boolean matches(RecipeInput input,Level level){if(input.size()<inputItems().size())return false;for(int i=0;i<inputItems().size();i++)if(!inputItems().get(i).matches(input.getItem(i),true))return false;return true;}
    @Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return getResultItem(lookup).copy();}
    @Override public ItemStack getResultItem(HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().pool().getFirst().stack();}
    @Override public boolean isSpecial(){return true;}
    @Override public boolean canCraftInDimensions(int width,int height){return true;}
    @Override public NonNullList<Ingredient> getIngredients(){var list=NonNullList.<Ingredient>create();for(var input:inputItems())list.add(input.ingredient());return list;}
    @Override public RecipeType<?> getType(){return CustomMachineContent.TYPE.get();}
    @Override public RecipeSerializer<?> getSerializer(){return CustomMachineContent.SERIALIZER.get();}
    public static final class Serializer implements RecipeSerializer<CustomMachineRecipe> {
        @Override public MapCodec<CustomMachineRecipe> codec(){return CODEC;}
        @Override public StreamCodec<RegistryFriendlyByteBuf,CustomMachineRecipe> streamCodec(){return StreamCodec.of((b,r)->{b.writeUtf(r.recipeKey);GenericRecipeData.STREAM_CODEC.encode(b,r.data);b.writeUtf(r.pollutionType);b.writeFloat(r.pollutionAmount);b.writeFloat(r.radiationAmount);b.writeInt(r.flux);b.writeInt(r.heat);},b->new CustomMachineRecipe(b.readUtf(),GenericRecipeData.STREAM_CODEC.decode(b),b.readUtf(),b.readFloat(),b.readFloat(),b.readInt(),b.readInt()));}
    }
}
