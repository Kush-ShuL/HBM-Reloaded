package com.hbm.client.exposurechamber;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.machine.exposurechamber.*;
import java.util.List;
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
public final class ExposureChamberJeiPlugin implements IModPlugin {
 static final RecipeType<ExposureChamberRecipe> TYPE=RecipeType.create("hbm","exposure_chamber",ExposureChamberRecipe.class);
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:exposure_chamber_jei");}@Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}@Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,ExposureChamberRecipes.recipes(level.getRecipeManager()));}@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,ExposureChamberContent.ITEM.get());}@Override public void registerGuiHandlers(IGuiHandlerRegistration event){event.addRecipeClickArea(ExposureChamberScreen.class,31,28,41,10,TYPE);}
 private static final class Category implements IRecipeCategory<ExposureChamberRecipe>{final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(ExposureChamberContent.ITEM.get()));}@Override public RecipeType<ExposureChamberRecipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.exposureChamber");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}@Override public void setRecipe(IRecipeLayoutBuilder builder,ExposureChamberRecipe recipe,IFocusGroup focuses){var display=ExposureChamberViews.drawing(recipe);var in=ExposureChamberViews.inputs(recipe);var out=ExposureChamberViews.outputs(recipe);var ip=LegacyRecipeDisplays.inputCoordinates(ExposureChamberViews.CATEGORY,in.size());var op=LegacyRecipeDisplays.outputCoordinates(ExposureChamberViews.CATEGORY,display);for(int i=0;i<in.size();i++)builder.addSlot(RecipeIngredientRole.INPUT,ip[i][0],ip[i][1]).addItemStacks(in.get(i));for(int i=0;i<out.size();i++)builder.addSlot(RecipeIngredientRole.OUTPUT,op[i][0],op[i][1]).addItemStacks(out.get(i));builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStacks(List.of(new ItemStack(ExposureChamberContent.ITEM.get())));}@Override public void draw(ExposureChamberRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){ExposureChamberViews.draw(graphics,recipe,0,0);}}
}
