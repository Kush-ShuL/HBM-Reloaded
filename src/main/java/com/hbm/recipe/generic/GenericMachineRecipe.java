package com.hbm.recipe.generic;
import com.hbm.fluid.FluidStack;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
/** Shared original GenericRecipe contract; each machine retains its own native RecipeType and serializer. */
public interface GenericMachineRecipe {
    GenericRecipeData data();
    default String name(){return data().name();} default int duration(){return data().duration();}default long power(){return data().power();}
    default List<GenericIngredient> inputItems(){return data().inputItems();}default List<FluidStack> inputFluids(){return data().inputFluids();}
    default List<GenericOutput> outputItems(){return data().outputItems();}default List<FluidStack> outputFluids(){return data().outputFluids();}
    default List<String> pools(){return data().pools();}default String group(){return data().group();}default ItemStack icon(){return data().resolvedIcon();}
    default Component displayName(){return data().displayName();}default boolean isPooled(){return !pools().isEmpty();}default boolean isPartOfPool(String pool){return pool!=null&&pools().contains(pool);}
    default List<Component> tooltip(){return data().tooltip();}
}
