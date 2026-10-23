package com.hbm.client.reactor.fuelpool;

import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.machine.reactor.fuelpool.*;
import mezz.jei.api.*;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public final class FuelPoolJeiPlugin implements IModPlugin {
    static final RecipeType<FuelPoolViews> TYPE=RecipeType.create("hbm","fuel_pool",FuelPoolViews.class);
    @Override public ResourceLocation getPluginUid(){return FuelPoolContent.id("fuel_pool_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){event.addRecipes(TYPE,FuelPoolViews.recipes());}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,FuelPoolContent.ITEM.get());}
    private static final class Category implements IRecipeCategory<FuelPoolViews>{
        private final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(FuelPoolContent.ITEM.get()));}
        @Override public RecipeType<FuelPoolViews> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.wasteDrum");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,FuelPoolViews recipe,IFocusGroup focus){var input=LegacyRecipeDisplays.inputCoordinates(FuelPoolViews.CATEGORY,1)[0];var output=LegacyRecipeDisplays.outputCoordinates(FuelPoolViews.CATEGORY,recipe.display())[0];builder.addSlot(RecipeIngredientRole.INPUT,input[0],input[1]).addItemStacks(recipe.inputs());builder.addSlot(RecipeIngredientRole.OUTPUT,output[0],output[1]).addItemStack(recipe.output());builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(FuelPoolContent.ITEM.get()));}
        @Override public void draw(FuelPoolViews recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){recipe.draw(graphics,0,0);}
    }
}
