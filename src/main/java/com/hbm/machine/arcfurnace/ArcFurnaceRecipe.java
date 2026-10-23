package com.hbm.machine.arcfurnace;
import com.hbm.material.*;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
/** A source input may have separate solid and molten outputs; it is never a vanilla smelting replacement. */
public record ArcFurnaceRecipe(Ingredient input,int inputCount,ItemStack solid,List<MaterialStack> molten,int order) implements Recipe<RecipeInput> {
 private static final Codec<MaterialStack> MATERIAL=RecordCodecBuilder.create(i->i.group(Codec.INT.fieldOf("material").forGetter((MaterialStack s)->s.material.id()),Codec.INT.fieldOf("amount").forGetter(s->s.amount)).apply(i,(id,amount)->new MaterialStack(Objects.requireNonNull(HbmMaterials.byId(id)),amount)));
 public static final MapCodec<ArcFurnaceRecipe> CODEC=RecordCodecBuilder.mapCodec(i->i.group(Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(ArcFurnaceRecipe::input),Codec.INT.optionalFieldOf("input_count",1).forGetter(ArcFurnaceRecipe::inputCount),ItemStack.OPTIONAL_CODEC.optionalFieldOf("solid",ItemStack.EMPTY).forGetter(ArcFurnaceRecipe::solid),MATERIAL.listOf().optionalFieldOf("molten",List.of()).forGetter(ArcFurnaceRecipe::molten),Codec.INT.optionalFieldOf("order",0).forGetter(ArcFurnaceRecipe::order)).apply(i,ArcFurnaceRecipe::new));
 public ArcFurnaceRecipe(Ingredient input,ItemStack solid,List<MaterialStack> molten,int order){this(input,1,solid,molten,order);}
 public ArcFurnaceRecipe{solid=solid.copy();molten=molten.stream().map(MaterialStack::copy).toList();}
 public boolean inMode(boolean liquid){return liquid?!molten.isEmpty():!solid.isEmpty();}
 @Override public boolean matches(RecipeInput input,Level level){return input.size()>0&&com.hbm.ore.OreRecipeInputs.test(this.input,input.getItem(0));}@Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return solid.copy();}@Override public ItemStack getResultItem(HolderLookup.Provider lookup){return solid.copy();}@Override public boolean canCraftInDimensions(int width,int height){return false;}@Override public boolean isSpecial(){return true;}@Override public RecipeType<?> getType(){return ArcFurnaceContent.RECIPE_TYPE.get();}@Override public RecipeSerializer<?> getSerializer(){return ArcFurnaceContent.SERIALIZER.get();}
 public static final class Serializer implements RecipeSerializer<ArcFurnaceRecipe> {
  @Override public MapCodec<ArcFurnaceRecipe> codec(){return CODEC;}@Override public StreamCodec<RegistryFriendlyByteBuf,ArcFurnaceRecipe> streamCodec(){return StreamCodec.of((b,r)->{Ingredient.CONTENTS_STREAM_CODEC.encode(b,r.input);b.writeInt(r.inputCount);ItemStack.OPTIONAL_STREAM_CODEC.encode(b,r.solid);b.writeVarInt(r.molten.size());for(var m:r.molten){b.writeInt(m.material.id());b.writeInt(m.amount);}b.writeInt(r.order);},b->{var input=Ingredient.CONTENTS_STREAM_CODEC.decode(b);int inputCount=b.readInt();var solid=ItemStack.OPTIONAL_STREAM_CODEC.decode(b);int count=b.readVarInt();var mats=new ArrayList<MaterialStack>();for(int n=0;n<count;n++)mats.add(new MaterialStack(Objects.requireNonNull(HbmMaterials.byId(b.readInt())),b.readInt()));return new ArcFurnaceRecipe(input,inputCount,solid,mats,b.readInt());});}
 }
}
