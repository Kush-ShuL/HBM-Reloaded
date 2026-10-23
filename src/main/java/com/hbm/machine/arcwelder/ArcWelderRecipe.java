package com.hbm.machine.arcwelder;

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

/** Three unordered source inputs. Extra occupied stacks fail; each ingredient occupies one whole stack. */
public record ArcWelderRecipe(GenericRecipeData data) implements Recipe<RecipeInput>,GenericMachineRecipe {
    public static final MapCodec<ArcWelderRecipe> CODEC=RecordCodecBuilder.mapCodec(i->i.group(GenericRecipeData.CODEC.fieldOf("data").forGetter(ArcWelderRecipe::data)).apply(i,ArcWelderRecipe::new));
    public ItemStack output(){return outputItems().getFirst().pool().getFirst().stack();}
    public com.hbm.fluid.FluidStack fluid(){return inputFluids().isEmpty()?null:inputFluids().getFirst();}
    public static boolean lane(List<GenericIngredient> ingredients,java.util.function.IntFunction<ItemStack> stacks,int start,int end){
        var remaining=new ArrayList<>(ingredients);
        for(int slot=start;slot<end;slot++){var stack=stacks.apply(slot);if(stack.isEmpty())continue;boolean matched=false;for(int i=0;i<remaining.size();i++)if(remaining.get(i).matches(stack,false)){remaining.remove(i);matched=true;break;}if(!matched)return false;}
        return remaining.isEmpty();
    }
    public boolean accepts(java.util.function.IntFunction<ItemStack> stacks){return lane(inputItems(),stacks,0,3);}
    @Override public boolean matches(RecipeInput input,Level level){return input.size()>=3&&accepts(input::getItem);}
    @Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return output().copy();}
    @Override public ItemStack getResultItem(HolderLookup.Provider lookup){return output();}
    @Override public NonNullList<Ingredient> getIngredients(){var result=NonNullList.<Ingredient>create();inputItems().forEach(in->result.add(in.ingredient()));return result;}
    @Override public boolean canCraftInDimensions(int width,int height){return width*height>=3;}
    @Override public boolean isSpecial(){return true;}
    @Override public RecipeType<?> getType(){return ArcWelderContent.TYPE.get();}
    @Override public RecipeSerializer<?> getSerializer(){return ArcWelderContent.SERIALIZER.get();}
    public static final class Serializer implements RecipeSerializer<ArcWelderRecipe>{
        @Override public MapCodec<ArcWelderRecipe> codec(){return CODEC;}
        @Override public StreamCodec<RegistryFriendlyByteBuf,ArcWelderRecipe> streamCodec(){return StreamCodec.of((buf,r)->{GenericRecipeData.STREAM_CODEC.encode(buf,r.data);},buf->new ArcWelderRecipe(GenericRecipeData.STREAM_CODEC.decode(buf)));}
    }
}
