package com.hbm.machine.rotaryfurnace;
import com.hbm.material.*;
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
/** Exact source unordered one-input-stack-per-AStack selection; no extra stacks accepted. */
public record RotaryFurnaceRecipe(GenericRecipeData data,MaterialStack molten,int steam) implements Recipe<RecipeInput>,GenericMachineRecipe {
 private static final Codec<MaterialStack> MATERIAL=RecordCodecBuilder.create(i->i.group(Codec.INT.fieldOf("material").forGetter((MaterialStack s)->s.material.id()),Codec.INT.fieldOf("amount").forGetter(s->s.amount)).apply(i,(id,amount)->new MaterialStack(Objects.requireNonNull(HbmMaterials.byId(id)),amount)));
 public static final MapCodec<RotaryFurnaceRecipe> CODEC=RecordCodecBuilder.mapCodec(i->i.group(GenericRecipeData.CODEC.fieldOf("data").forGetter(RotaryFurnaceRecipe::data),MATERIAL.fieldOf("molten").forGetter(RotaryFurnaceRecipe::molten),Codec.INT.fieldOf("steam").forGetter(RotaryFurnaceRecipe::steam)).apply(i,RotaryFurnaceRecipe::new));
 public RotaryFurnaceRecipe{molten=molten.copy();}
 public boolean accepts(com.hbm.fluid.HbmFluidType type,ItemStack... stacks){if(!inputFluids().isEmpty()&&inputFluids().getFirst().type!=type)return false;var remaining=new ArrayList<>(inputItems());for(var stack:stacks)if(!stack.isEmpty()){boolean match=false;for(var ingredient:remaining)if(ingredient.matchesProcessing(stack,false)){remaining.remove(ingredient);match=true;break;}if(!match)return false;}return remaining.isEmpty();}
 /** Source consumeItems searches slot0..2 again per ingredient; do not replace with an allocation from selection. */
 public int[] consumed(ItemStack... stacks){var copy=Arrays.stream(stacks).map(ItemStack::copy).toArray(ItemStack[]::new);var consumed=new int[stacks.length];for(var ingredient:inputItems())for(int i=0;i<copy.length;i++)if(ingredient.matchesProcessing(copy[i],false)){copy[i].shrink(ingredient.count());consumed[i]+=ingredient.count();break;}return consumed;}
 @Override public boolean matches(RecipeInput input,Level level){var stacks=new ItemStack[input.size()];for(int i=0;i<stacks.length;i++)stacks[i]=input.getItem(i);return accepts(com.hbm.fluid.HbmFluids.NONE,stacks);}@Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return com.hbm.item.autogen.ScrapsItem.create(molten,true);}@Override public ItemStack getResultItem(HolderLookup.Provider lookup){return com.hbm.item.autogen.ScrapsItem.create(molten,true);}@Override public boolean canCraftInDimensions(int w,int h){return true;}@Override public boolean isSpecial(){return true;}@Override public NonNullList<Ingredient> getIngredients(){var values=NonNullList.<Ingredient>create();inputItems().forEach(in->values.add(in.ingredient()));return values;}@Override public RecipeType<?> getType(){return RotaryFurnaceContent.TYPE.get();}@Override public RecipeSerializer<?> getSerializer(){return RotaryFurnaceContent.SERIALIZER.get();}
 public static final class Serializer implements RecipeSerializer<RotaryFurnaceRecipe>{@Override public MapCodec<RotaryFurnaceRecipe> codec(){return CODEC;}@Override public StreamCodec<RegistryFriendlyByteBuf,RotaryFurnaceRecipe> streamCodec(){return StreamCodec.of((b,r)->{GenericRecipeData.STREAM_CODEC.encode(b,r.data);b.writeInt(r.molten.material.id());b.writeInt(r.molten.amount);b.writeInt(r.steam);},b->new RotaryFurnaceRecipe(GenericRecipeData.STREAM_CODEC.decode(b),new MaterialStack(Objects.requireNonNull(HbmMaterials.byId(b.readInt())),b.readInt()),b.readInt()));}}
}
