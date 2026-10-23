package com.hbm.machine.assembler;
import com.hbm.recipe.generic.*;
import com.hbm.fluid.FluidStack;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
/** Native positional GenericRecipe; source-mode choices are fixed by server before recipe synchronization. */
public record AssemblerRecipe(GenericRecipeData data,Optional<List<GenericIngredient>> expensiveItems,Optional<List<FluidStack>> expensiveFluids,List<String> pools528,boolean requiresNo528) implements Recipe<RecipeInput>,GenericMachineRecipe {
    public static final MapCodec<AssemblerRecipe> CODEC=RecordCodecBuilder.mapCodec(i->i.group(GenericRecipeData.MAP_CODEC.forGetter(AssemblerRecipe::data),GenericIngredient.CODEC.listOf().optionalFieldOf("expensive_input_items").forGetter(AssemblerRecipe::expensiveItems),GenericRecipeData.FLUID_CODEC.listOf().optionalFieldOf("expensive_input_fluids").forGetter(AssemblerRecipe::expensiveFluids),Codec.STRING.listOf().optionalFieldOf("pools_528",List.of()).forGetter(AssemblerRecipe::pools528),Codec.BOOL.optionalFieldOf("requires_no_528",false).forGetter(AssemblerRecipe::requiresNo528)).apply(i,AssemblerRecipe::new));
    public AssemblerRecipe(GenericRecipeData data){this(data,Optional.empty(),Optional.empty(),List.of(),false);}
    public AssemblerRecipe configured(boolean expensive,boolean mode528){var r=data;return new AssemblerRecipe(new GenericRecipeData(r.name(),r.duration(),r.power(),expensive?expensiveItems.orElse(r.inputItems()):r.inputItems(),expensive?expensiveFluids.orElse(r.inputFluids()):r.inputFluids(),r.outputItems(),r.outputFluids(),mode528&&!pools528.isEmpty()?pools528:r.pools(),r.group(),r.icon(),r.named(),r.nameWrapper(),r.order()));}
    @Override public boolean matches(RecipeInput input,Level level){if(input.size()<inputItems().size())return false;for(int i=0;i<inputItems().size();i++)if(!inputItems().get(i).matches(input.getItem(i),false))return false;return true;}
    @Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().collapse(net.minecraft.util.RandomSource.create());}
    @Override public ItemStack getResultItem(HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().pool().getFirst().stack();}
    @Override public boolean canCraftInDimensions(int width,int height){return true;}@Override public boolean isSpecial(){return true;}
    @Override public NonNullList<Ingredient> getIngredients(){var result=NonNullList.<Ingredient>create();inputItems().forEach(in->result.add(in.ingredient()));return result;}
    @Override public RecipeType<?> getType(){return AssemblerContent.TYPE.get();}@Override public RecipeSerializer<?> getSerializer(){return AssemblerContent.SERIALIZER.get();}
    public static final class Serializer implements RecipeSerializer<AssemblerRecipe>{@Override public MapCodec<AssemblerRecipe> codec(){return CODEC;}@Override public StreamCodec<RegistryFriendlyByteBuf,AssemblerRecipe> streamCodec(){return GenericRecipeData.STREAM_CODEC.map(AssemblerRecipe::new,AssemblerRecipe::data);}}
}
