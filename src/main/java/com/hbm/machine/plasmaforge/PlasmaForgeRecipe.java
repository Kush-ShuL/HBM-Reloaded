package com.hbm.machine.plasmaforge;
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
/** Original positional plasma recipe: ignition temperature is distinct from its per-tick HE cost. */
public record PlasmaForgeRecipe(GenericRecipeData data,long ignitionTemp,Optional<List<GenericIngredient>> expensiveItems,Optional<List<FluidStack>> expensiveFluids,List<String> pools528) implements Recipe<RecipeInput>,GenericMachineRecipe {
    public static final MapCodec<PlasmaForgeRecipe> CODEC=RecordCodecBuilder.mapCodec(i->i.group(GenericRecipeData.MAP_CODEC.forGetter(PlasmaForgeRecipe::data),Codec.LONG.fieldOf("ignition_temp").forGetter(PlasmaForgeRecipe::ignitionTemp),GenericIngredient.CODEC.listOf().optionalFieldOf("expensive_input_items").forGetter(PlasmaForgeRecipe::expensiveItems),GenericRecipeData.FLUID_CODEC.listOf().optionalFieldOf("expensive_input_fluids").forGetter(PlasmaForgeRecipe::expensiveFluids),Codec.STRING.listOf().optionalFieldOf("pools_528",List.of()).forGetter(PlasmaForgeRecipe::pools528)).apply(i,PlasmaForgeRecipe::new));
    public PlasmaForgeRecipe(GenericRecipeData data,long ignition){this(data,ignition,Optional.empty(),Optional.empty(),List.of());}
    public PlasmaForgeRecipe configured(boolean expensive){var r=data;return new PlasmaForgeRecipe(new GenericRecipeData(r.name(),r.duration(),r.power(),expensive?expensiveItems.orElse(r.inputItems()):r.inputItems(),expensive?expensiveFluids.orElse(r.inputFluids()):r.inputFluids(),r.outputItems(),r.outputFluids(),com.hbm.config.GeneralModes.mode528()&&!pools528.isEmpty()?pools528:r.pools(),r.group(),r.icon(),r.named(),r.nameWrapper(),r.order()),ignitionTemp);}
    @Override public boolean matches(RecipeInput input,Level level){if(input.size()<inputItems().size())return false;for(int i=0;i<inputItems().size();i++)if(!inputItems().get(i).matches(input.getItem(i),false))return false;return true;}
    @Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().collapse(net.minecraft.util.RandomSource.create());}
    @Override public ItemStack getResultItem(HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().pool().getFirst().stack();}
    @Override public boolean canCraftInDimensions(int width,int height){return true;}
    @Override public boolean isSpecial(){return true;}
    @Override public NonNullList<Ingredient> getIngredients(){var result=NonNullList.<Ingredient>create();inputItems().forEach(in->result.add(in.ingredient()));return result;}
    @Override public RecipeType<?> getType(){return PlasmaForgeContent.TYPE.get();}
    @Override public RecipeSerializer<?> getSerializer(){return PlasmaForgeContent.SERIALIZER.get();}
    @Override public java.util.List<net.minecraft.network.chat.Component> tooltip(){var lines=new java.util.ArrayList<>(GenericMachineRecipe.super.tooltip());lines.add(3,net.minecraft.network.chat.Component.translatable("gui.recipe.plasmaIn").append(": "+com.hbm.item.battery.BatteryItem.shortNumber(ignitionTemp)+"TU/t"));return lines;}
    public static final class Serializer implements RecipeSerializer<PlasmaForgeRecipe>{
        @Override public MapCodec<PlasmaForgeRecipe> codec(){return CODEC;}
        @Override public StreamCodec<RegistryFriendlyByteBuf,PlasmaForgeRecipe> streamCodec(){return StreamCodec.of((buffer,recipe)->{recipe.data.encode(buffer);buffer.writeLong(recipe.ignitionTemp);},buffer->new PlasmaForgeRecipe(GenericRecipeData.decode(buffer),buffer.readLong()));}
    }
}
