package com.hbm.machine.ammopress;
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
/** True nine-cell source grid; empty cells reject extra items and each count is tested in its own stack. */
public record AmmoPressRecipe(GenericRecipeData data,List<Integer> cells) implements Recipe<RecipeInput>,GenericMachineRecipe {
 public static final MapCodec<AmmoPressRecipe> CODEC=RecordCodecBuilder.mapCodec(i->i.group(GenericRecipeData.CODEC.fieldOf("data").forGetter(AmmoPressRecipe::data),Codec.INT.listOf().fieldOf("cells").forGetter(AmmoPressRecipe::cells)).apply(i,AmmoPressRecipe::new));
 public AmmoPressRecipe{cells=List.copyOf(cells);if(cells.size()!=9||cells.stream().anyMatch(i->i< -1||i>=data.inputItems().size()))throw new IllegalArgumentException("Ammo press requires exactly nine valid grid cells");if(data.inputItems().isEmpty()||data.inputItems().stream().anyMatch(i->i.count()<=0)||data.outputItems().size()!=1||data.outputItems().getFirst().single().isEmpty())throw new IllegalArgumentException("Invalid source ammo press formula");}
 public GenericIngredient cell(int slot){int index=cells.get(slot);return index<0?null:inputItems().get(index);}
 public ItemStack output(){return outputItems().getFirst().single();}
 public boolean accepts(java.util.function.IntFunction<ItemStack> items){for(int i=0;i<9;i++){var ingredient=cell(i);var held=items.apply(i);if(ingredient==null?!held.isEmpty():!ingredient.matches(held,false))return false;}return true;}
 @Override public boolean matches(RecipeInput input,Level level){return input.size()>=9&&accepts(input::getItem);}@Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return output();}@Override public ItemStack getResultItem(HolderLookup.Provider lookup){return output();}
 @Override public NonNullList<Ingredient> getIngredients(){var result=NonNullList.<Ingredient>create();for(int i=0;i<9;i++){var cell=cell(i);result.add(cell==null?Ingredient.EMPTY:cell.ingredient());}return result;}
 @Override public boolean canCraftInDimensions(int width,int height){return width>=3&&height>=3;}@Override public boolean isSpecial(){return true;}@Override public RecipeType<?> getType(){return AmmoPressContent.TYPE.get();}@Override public RecipeSerializer<?> getSerializer(){return AmmoPressContent.SERIALIZER.get();}
 public static final class Serializer implements RecipeSerializer<AmmoPressRecipe>{
  @Override public MapCodec<AmmoPressRecipe> codec(){return CODEC;}@Override public StreamCodec<RegistryFriendlyByteBuf,AmmoPressRecipe> streamCodec(){return StreamCodec.of((buf,r)->{GenericRecipeData.STREAM_CODEC.encode(buf,r.data);for(int index:r.cells)buf.writeInt(index);},buf->{var data=GenericRecipeData.STREAM_CODEC.decode(buf);var cells=new ArrayList<Integer>();for(int i=0;i<9;i++)cells.add(buf.readInt());return new AmmoPressRecipe(data,cells);});}
 }
}
