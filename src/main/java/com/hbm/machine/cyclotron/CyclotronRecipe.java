package com.hbm.machine.cyclotron;
import com.hbm.recipe.generic.*;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
/** One genuine particle/target pair, native synced data and the source HashMap key hash. */
public record CyclotronRecipe(GenericRecipeData data,String sourceKey,int sourceHash) implements Recipe<RecipeInput>,GenericMachineRecipe {
 public static final MapCodec<CyclotronRecipe> CODEC=RecordCodecBuilder.mapCodec(i->i.group(GenericRecipeData.CODEC.fieldOf("data").forGetter(CyclotronRecipe::data),Codec.STRING.fieldOf("source_key").forGetter(CyclotronRecipe::sourceKey),Codec.INT.fieldOf("source_hash").forGetter(CyclotronRecipe::sourceHash)).apply(i,CyclotronRecipe::new));
 public boolean matches(ItemStack particle,ItemStack target){return inputItems().get(0).matches(particle,true)&&inputItems().get(1).matches(target,true);}
 public ItemStack output(){return outputItems().getFirst().single();}public int antimatter(){return outputFluids().getFirst().fill;}
 @Override public boolean matches(RecipeInput input,Level level){return input.size()>=2&&matches(input.getItem(0),input.getItem(1));}
 @Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return output();}@Override public ItemStack getResultItem(HolderLookup.Provider lookup){return output();}@Override public boolean canCraftInDimensions(int x,int y){return true;}@Override public boolean isSpecial(){return true;}@Override public RecipeType<?> getType(){return CyclotronContent.TYPE.get();}@Override public RecipeSerializer<?> getSerializer(){return CyclotronContent.SERIALIZER.get();}
 public static final class Serializer implements RecipeSerializer<CyclotronRecipe>{@Override public MapCodec<CyclotronRecipe> codec(){return CODEC;}@Override public StreamCodec<RegistryFriendlyByteBuf,CyclotronRecipe> streamCodec(){return StreamCodec.of((b,r)->{r.data.encode(b);b.writeUtf(r.sourceKey);b.writeInt(r.sourceHash);},b->new CyclotronRecipe(GenericRecipeData.decode(b),b.readUtf(),b.readInt()));}}
}
