package com.hbm.client.oilcatalyticcracker;
import com.hbm.machine.oilcatalyticcracker.*;
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
@JeiPlugin public final class CatalyticCrackerJeiPlugin implements IModPlugin {
 static final RecipeType<CatalyticCrackerRecipe> TYPE=RecipeType.create("hbm","catalytic_cracking",CatalyticCrackerRecipe.class);@Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:catalytic_cracking_jei");}@Override public void registerCategories(IRecipeCategoryRegistration e){e.addRecipeCategories(new Category(e.getJeiHelpers().getGuiHelper()));}@Override public void registerRecipes(IRecipeRegistration e){var level=Minecraft.getInstance().level;if(level!=null)e.addRecipes(TYPE,CatalyticCrackerRecipes.recipes(level.getRecipeManager()));}@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration e){e.addRecipeCatalysts(TYPE,CatalyticCrackerContent.ITEM.get());}
 private static final class Category implements IRecipeCategory<CatalyticCrackerRecipe>{private final IDrawable background,icon;Category(IGuiHelper h){background=h.createBlankDrawable(166,65);icon=h.createDrawableItemStack(new ItemStack(CatalyticCrackerContent.ITEM.get()));}@Override public RecipeType<CatalyticCrackerRecipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("gui.hbm.catalytic_cracker.recipe");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
  @Override public void setRecipe(IRecipeLayoutBuilder builder,CatalyticCrackerRecipe recipe,IFocusGroup focus){var inputs=CatalyticCrackerViews.inputs(recipe);for(int i=0;i<inputs.size();i++)builder.addSlot(RecipeIngredientRole.INPUT,30+i*18,24).addItemStack(inputs.get(i));var outputs=CatalyticCrackerViews.outputs(recipe);for(int i=0;i<outputs.size();i++)builder.addSlot(RecipeIngredientRole.OUTPUT,102+i*18,24).addItemStack(outputs.get(i));builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(CatalyticCrackerContent.ITEM.get()));}@Override public void draw(CatalyticCrackerRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){CatalyticCrackerViews.draw(recipe,graphics,0,0);}
 }
}
