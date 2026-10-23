package com.hbm.client.machine.pa.cooled;
import com.hbm.machine.pa.cooled.*;
import com.hbm.machine.pa.recipe.*;
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
public final class PaJeiPlugin implements IModPlugin{
 static final RecipeType<PaRecipe> TYPE=RecipeType.create("hbm","particle_accelerator",PaRecipe.class);@Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:particle_accelerator_jei");}@Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}@Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,PaRecipes.recipes(level.getRecipeManager()));}@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,PaContent.ITEM.get(PaKind.DETECTOR).get());}
 private static final class Category implements IRecipeCategory<PaRecipe>{final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(PaContent.ITEM.get(PaKind.DETECTOR).get()));}@Override public RecipeType<PaRecipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.pa_detector");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}@Override public void setRecipe(IRecipeLayoutBuilder builder,PaRecipe recipe,IFocusGroup focus){builder.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStacks(recipe.input1().examples());builder.addSlot(RecipeIngredientRole.INPUT,30,24).addItemStacks(recipe.input2().examples());builder.addSlot(RecipeIngredientRole.OUTPUT,102,24).addItemStack(recipe.output1());recipe.output2().ifPresent(value->builder.addSlot(RecipeIngredientRole.OUTPUT,120,24).addItemStack(value));builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(PaContent.ITEM.get(PaKind.DETECTOR).get()));}@Override public void draw(PaRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){PaRecipeViews.draw(recipe,graphics,0,0);}}
}
