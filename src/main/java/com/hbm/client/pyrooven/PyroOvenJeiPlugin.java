package com.hbm.client.pyrooven;
import com.hbm.machine.pyrooven.*;
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
public final class PyroOvenJeiPlugin implements IModPlugin {
 static final RecipeType<PyroOvenRecipe> TYPE=RecipeType.create("hbm","pyrooven",PyroOvenRecipe.class);
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:pyrooven_jei");}@Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}@Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,PyroOvenRecipes.recipes(level.getRecipeManager()).stream().filter(r->!PyroOvenViews.inputs(r).isEmpty()&&!PyroOvenViews.outputs(r).isEmpty()).toList());}@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,PyroOvenContent.ITEM.get());}@Override public void registerGuiHandlers(IGuiHandlerRegistration event){event.addRecipeClickArea(PyroOvenScreen.class,50,35,28,14,TYPE);}
 private static final class Category implements IRecipeCategory<PyroOvenRecipe>{final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(PyroOvenContent.ITEM.get()));}@Override public RecipeType<PyroOvenRecipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.machinePyroOven");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
  @Override public void setRecipe(IRecipeLayoutBuilder builder,PyroOvenRecipe recipe,IFocusGroup focus){var in=PyroOvenViews.inputs(recipe);for(int i=0;i<in.size();i++)builder.addSlot(RecipeIngredientRole.INPUT,48-i*18,24).addItemStacks(in.get(i));var out=PyroOvenViews.outputs(recipe);for(int i=0;i<out.size();i++)builder.addSlot(RecipeIngredientRole.OUTPUT,102+i*18,24).addItemStack(out.get(i));builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(PyroOvenContent.ITEM.get()));}
  @Override public void draw(PyroOvenRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){PyroOvenViews.draw(recipe,graphics,0,0);}
 }
}
