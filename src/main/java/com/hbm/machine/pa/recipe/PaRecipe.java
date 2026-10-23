package com.hbm.machine.pa.recipe;
import com.hbm.recipe.generic.GenericIngredient;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
/** Original symmetric two-input momentum threshold recipe, not an invented timed processing recipe. */
public record PaRecipe(GenericIngredient input1,GenericIngredient input2,int momentum,ItemStack output1,Optional<ItemStack> output2,int order)implements Recipe<RecipeInput>{
    public static final MapCodec<PaRecipe> CODEC=RecordCodecBuilder.mapCodec(i->i.group(GenericIngredient.CODEC.fieldOf("input1").forGetter(PaRecipe::input1),GenericIngredient.CODEC.fieldOf("input2").forGetter(PaRecipe::input2),Codec.INT.fieldOf("momentum").forGetter(PaRecipe::momentum),ItemStack.CODEC.fieldOf("output1").forGetter(PaRecipe::output1),ItemStack.CODEC.optionalFieldOf("output2").forGetter(PaRecipe::output2),Codec.INT.optionalFieldOf("order",0).forGetter(PaRecipe::order)).apply(i,PaRecipe::new));
    public boolean matches(ItemStack one,ItemStack two){return input1.matches(one,true)&&input2.matches(two,true)||input1.matches(two,true)&&input2.matches(one,true);}
    @Override public boolean matches(RecipeInput input,Level level){return input.size()>1&&matches(input.getItem(0),input.getItem(1));}@Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return output1.copy();}@Override public ItemStack getResultItem(HolderLookup.Provider lookup){return output1;}@Override public boolean canCraftInDimensions(int x,int y){return true;}@Override public boolean isSpecial(){return true;}@Override public RecipeType<?> getType(){return PaRecipeContent.TYPE.get();}@Override public RecipeSerializer<?> getSerializer(){return PaRecipeContent.SERIALIZER.get();}
    public static final class Serializer implements RecipeSerializer<PaRecipe>{@Override public MapCodec<PaRecipe> codec(){return CODEC;}@Override public StreamCodec<RegistryFriendlyByteBuf,PaRecipe> streamCodec(){return StreamCodec.of((b,r)->{r.input1.encode(b);r.input2.encode(b);b.writeInt(r.momentum);ItemStack.STREAM_CODEC.encode(b,r.output1);ItemStack.OPTIONAL_STREAM_CODEC.encode(b,r.output2.orElse(ItemStack.EMPTY));b.writeInt(r.order);},b->{var one=GenericIngredient.decode(b);var two=GenericIngredient.decode(b);int momentum=b.readInt();var output1=ItemStack.STREAM_CODEC.decode(b);var output2=ItemStack.OPTIONAL_STREAM_CODEC.decode(b);return new PaRecipe(one,two,momentum,output1,output2.isEmpty()?Optional.empty():Optional.of(output2),b.readInt());});}}
}
