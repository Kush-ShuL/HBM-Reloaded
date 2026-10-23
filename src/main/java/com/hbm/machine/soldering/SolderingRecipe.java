package com.hbm.machine.soldering;

import com.hbm.recipe.generic.*;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/** Three independent source lanes. Extra occupied inputs fail, quantities must occur in one stack. */
public record SolderingRecipe(GenericRecipeData data,int toppingCount,int pcbCount) implements Recipe<RecipeInput>,GenericMachineRecipe {
    public static final MapCodec<SolderingRecipe> CODEC=RecordCodecBuilder.mapCodec(i->i.group(GenericRecipeData.CODEC.fieldOf("data").forGetter(SolderingRecipe::data),com.mojang.serialization.Codec.INT.fieldOf("toppingCount").forGetter(SolderingRecipe::toppingCount),com.mojang.serialization.Codec.INT.fieldOf("pcbCount").forGetter(SolderingRecipe::pcbCount)).apply(i,SolderingRecipe::new));
    public List<GenericIngredient> toppings(){return inputItems().subList(0,toppingCount);}
    public List<GenericIngredient> pcb(){return inputItems().subList(toppingCount,toppingCount+pcbCount);}
    public List<GenericIngredient> solder(){return inputItems().subList(toppingCount+pcbCount,inputItems().size());}
    public ItemStack output(){return outputItems().getFirst().pool().getFirst().stack();}
    public com.hbm.fluid.FluidStack fluid(){return inputFluids().isEmpty()?null:inputFluids().getFirst();}
    public static boolean lane(List<GenericIngredient> ingredients,java.util.function.IntFunction<ItemStack> stacks,int start,int end){
        var remaining=new ArrayList<>(ingredients);
        for(int slot=start;slot<end;slot++){var stack=stacks.apply(slot);if(stack.isEmpty())continue;boolean matched=false;for(int i=0;i<remaining.size();i++)if(remaining.get(i).matches(stack,false)){remaining.remove(i);matched=true;break;}if(!matched)return false;}
        return remaining.isEmpty();
    }
    public boolean accepts(java.util.function.IntFunction<ItemStack> stacks){return lane(toppings(),stacks,0,3)&&lane(pcb(),stacks,3,5)&&lane(solder(),stacks,5,6);}
    @Override public boolean matches(RecipeInput input,Level level){return input.size()>=6&&accepts(input::getItem);}
    @Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return output().copy();}
    @Override public ItemStack getResultItem(HolderLookup.Provider lookup){return output();}
    @Override public NonNullList<Ingredient> getIngredients(){var result=NonNullList.<Ingredient>create();inputItems().forEach(in->result.add(in.ingredient()));return result;}
    @Override public boolean canCraftInDimensions(int width,int height){return width*height>=6;}
    @Override public boolean isSpecial(){return true;}
    @Override public RecipeType<?> getType(){return SolderingContent.TYPE.get();}
    @Override public RecipeSerializer<?> getSerializer(){return SolderingContent.SERIALIZER.get();}
    public static final class Serializer implements RecipeSerializer<SolderingRecipe>{
        @Override public MapCodec<SolderingRecipe> codec(){return CODEC;}
        @Override public StreamCodec<RegistryFriendlyByteBuf,SolderingRecipe> streamCodec(){return StreamCodec.of((buf,r)->{GenericRecipeData.STREAM_CODEC.encode(buf,r.data);buf.writeVarInt(r.toppingCount);buf.writeVarInt(r.pcbCount);},buf->new SolderingRecipe(GenericRecipeData.STREAM_CODEC.decode(buf),buf.readVarInt(),buf.readVarInt()));}
    }
}
