package com.hbm.client.chemicalplant;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.machine.chemicalplant.*;
import com.hbm.recipe.generic.*;
import java.util.*;
import mezz.jei.api.*;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
@JeiPlugin
public final class ChemicalPlantJeiPlugin implements IModPlugin {
    static final RecipeType<ChemicalPlantRecipe> TYPE=RecipeType.create("hbm","chemical",ChemicalPlantRecipe.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","chemical_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,ChemicalPlantRecipeConfig.recipes(level.getRecipeManager()).stream().filter(ChemicalPlantViews::visible).toList());}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,ChemicalPlantContent.ITEM.get(),com.hbm.machine.chemicalfactory.ChemicalFactoryContent.ITEM.get());}
    private static final class Category implements IRecipeCategory<ChemicalPlantRecipe>{
        final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(ChemicalPlantContent.ITEM.get()));}
        @Override public RecipeType<ChemicalPlantRecipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.machineChemicalPlant");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,ChemicalPlantRecipe recipe,IFocusGroup focuses){var display=ChemicalPlantViews.drawing(recipe);var in=ChemicalPlantViews.inputs(recipe);var out=ChemicalPlantViews.outputs(recipe);var ip=LegacyRecipeDisplays.inputCoordinates(ChemicalPlantViews.CATEGORY,in.size());var op=LegacyRecipeDisplays.outputCoordinates(ChemicalPlantViews.CATEGORY,display);for(int i=0;i<in.size();i++)builder.addSlot(RecipeIngredientRole.INPUT,ip[i][0],ip[i][1]).addItemStacks(in.get(i)).addTooltipCallback((slot,tooltip)->slot.getDisplayedItemStack().ifPresent(stack->tooltip.addAll(com.hbm.ore.OreRecipeInputs.tooltip(stack))));for(int i=0;i<out.size();i++){int lane=i;builder.addSlot(RecipeIngredientRole.OUTPUT,op[i][0],op[i][1]).addItemStacks(out.get(i)).addTooltipCallback((slot,tooltip)->slot.getDisplayedItemStack().ifPresent(stack->tooltip.addAll(ChemicalPlantViews.outputTooltip(recipe,lane,stack))));}int offset=ChemicalPlantViews.offset(recipe);if(recipe.isPooled())builder.addSlot(RecipeIngredientRole.CATALYST,75+offset,10).addItemStacks(ChemicalPlantViews.blueprints(recipe));builder.addSlot(RecipeIngredientRole.CATALYST,75+offset,recipe.isPooled()?38:31).addItemStacks(List.of(new ItemStack(ChemicalPlantContent.ITEM.get()),new ItemStack(com.hbm.machine.chemicalfactory.ChemicalFactoryContent.ITEM.get())));}
        @Override public void draw(ChemicalPlantRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){ChemicalPlantViews.draw(graphics,recipe,0,0);}
    }
}
