package com.hbm.machine.foundry.crucible;
import com.hbm.material.*;
import com.hbm.recipe.generic.*;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
/** Original molten-material alloy, with its own synchronized recipe type and no fake fluid/item processor. */
public record CrucibleRecipe(GenericRecipeData data,int frequency,List<MaterialStack> input,List<MaterialStack> output) implements Recipe<RecipeInput>,GenericMachineRecipe {
 private static final Codec<MaterialStack> MATERIAL=RecordCodecBuilder.create(i->i.group(Codec.INT.fieldOf("material").forGetter((MaterialStack s)->s.material.id()),Codec.INT.fieldOf("amount").forGetter(s->s.amount)).apply(i,(id,amount)->new MaterialStack(Objects.requireNonNull(HbmMaterials.byId(id)),amount)));
 public static final MapCodec<CrucibleRecipe> CODEC=RecordCodecBuilder.mapCodec(i->i.group(GenericRecipeData.CODEC.fieldOf("data").forGetter(CrucibleRecipe::data),Codec.INT.fieldOf("frequency").forGetter(CrucibleRecipe::frequency),MATERIAL.listOf().fieldOf("input").forGetter(CrucibleRecipe::input),MATERIAL.listOf().fieldOf("output").forGetter(CrucibleRecipe::output)).apply(i,CrucibleRecipe::new));
 public CrucibleRecipe{input=input.stream().map(MaterialStack::copy).toList();output=output.stream().map(MaterialStack::copy).toList();}
 public int inputAmount(){return input.stream().mapToInt(s->s.amount).sum();}
 @Override public Component displayName(){return Component.translatable(name());}
 @Override public List<Component> tooltip(){var lines=GenericRecipeData.tooltipHeader(displayName(),name());boolean showInMb=GenericRecipeData.detailsRequested();lines.add(Component.translatable("gui.recipe.input").withStyle(ChatFormatting.BOLD));for(var stack:input)lines.add(stack.material.displayName().copy().append(": ").append(HbmMaterials.formatAmount(stack.amount,showInMb)));lines.add(Component.translatable("gui.recipe.output").withStyle(ChatFormatting.BOLD));for(var stack:output)lines.add(stack.material.displayName().copy().append(": ").append(HbmMaterials.formatAmount(stack.amount,showInMb)));return lines;}
 @Override public boolean matches(RecipeInput input,Level level){return false;}@Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return ItemStack.EMPTY;}@Override public ItemStack getResultItem(HolderLookup.Provider lookup){return ItemStack.EMPTY;}@Override public boolean canCraftInDimensions(int w,int h){return false;}@Override public boolean isSpecial(){return true;}@Override public RecipeType<?> getType(){return CrucibleContent.TYPE.get();}@Override public RecipeSerializer<?> getSerializer(){return CrucibleContent.SERIALIZER.get();}
 private static void writeMaterials(RegistryFriendlyByteBuf b,List<MaterialStack> list){b.writeVarInt(list.size());for(var s:list){b.writeInt(s.material.id());b.writeInt(s.amount);}}private static List<MaterialStack> readMaterials(RegistryFriendlyByteBuf b){var list=new ArrayList<MaterialStack>();int count=b.readVarInt();for(int i=0;i<count;i++)list.add(new MaterialStack(Objects.requireNonNull(HbmMaterials.byId(b.readInt())),b.readInt()));return list;}
 public static final class Serializer implements RecipeSerializer<CrucibleRecipe>{@Override public MapCodec<CrucibleRecipe> codec(){return CODEC;}@Override public StreamCodec<RegistryFriendlyByteBuf,CrucibleRecipe> streamCodec(){return StreamCodec.of((b,r)->{GenericRecipeData.STREAM_CODEC.encode(b,r.data);b.writeInt(r.frequency);writeMaterials(b,r.input);writeMaterials(b,r.output);},b->new CrucibleRecipe(GenericRecipeData.STREAM_CODEC.decode(b),b.readInt(),readMaterials(b),readMaterials(b)));}}
}
