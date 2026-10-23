package com.hbm.machine.crystallizer;
import com.hbm.recipe.generic.*;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
/** Source duration/productivity/input quantity and exact/dictionary/wildcard priority, genuinely synchronized. */
public record CrystallizerRecipe(GenericRecipeData data,float productivity,String keyKind,String sourceKey)implements Recipe<RecipeInput>,GenericMachineRecipe {
 public static final MapCodec<CrystallizerRecipe> CODEC=RecordCodecBuilder.mapCodec(i->i.group(GenericRecipeData.MAP_CODEC.forGetter(CrystallizerRecipe::data),Codec.FLOAT.optionalFieldOf("productivity",0F).forGetter(CrystallizerRecipe::productivity),Codec.STRING.optionalFieldOf("key_kind","item").forGetter(CrystallizerRecipe::keyKind),Codec.STRING.optionalFieldOf("source_key","").forGetter(CrystallizerRecipe::sourceKey)).apply(i,CrystallizerRecipe::new));
 @Override public boolean matches(RecipeInput input,Level level){return input.size()>0&&inputItems().getFirst().matchesProcessing(input.getItem(0),false);}@Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return getResultItem(lookup).copy();}@Override public ItemStack getResultItem(HolderLookup.Provider lookup){return outputItems().getFirst().pool().getFirst().stack();}@Override public boolean isSpecial(){return true;}@Override public boolean canCraftInDimensions(int x,int y){return true;}@Override public RecipeType<?> getType(){return CrystallizerContent.TYPE.get();}@Override public RecipeSerializer<?> getSerializer(){return CrystallizerContent.SERIALIZER.get();}
 public static final class Serializer implements RecipeSerializer<CrystallizerRecipe>{@Override public MapCodec<CrystallizerRecipe> codec(){return CODEC;}@Override public StreamCodec<RegistryFriendlyByteBuf,CrystallizerRecipe> streamCodec(){return StreamCodec.of((b,r)->{r.data.encode(b);b.writeFloat(r.productivity);b.writeUtf(r.keyKind);b.writeUtf(r.sourceKey);},b->new CrystallizerRecipe(GenericRecipeData.decode(b),b.readFloat(),b.readUtf(),b.readUtf()));}}
}
