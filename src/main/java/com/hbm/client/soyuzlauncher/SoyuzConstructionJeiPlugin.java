package com.hbm.client.soyuzlauncher;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing;
import com.hbm.machine.soyuzlauncher.SoyuzLauncherContent;
import java.util.List;
import mezz.jei.api.*;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
@JeiPlugin public final class SoyuzConstructionJeiPlugin implements IModPlugin {
 static final RecipeType<LegacyRecipeDisplays.Display> TYPE=RecipeType.create("hbm","soyuz_construction",LegacyRecipeDisplays.Display.class);
 @Override public ResourceLocation getPluginUid(){return SoyuzLauncherContent.id("soyuz_construction_jei");}
 @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
 @Override public void registerRecipes(IRecipeRegistration event){event.addRecipes(TYPE,List.of(SoyuzConstruction.display()));}
 @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalyst(new ItemStack(SoyuzLauncherContent.STRUCTURE_ITEM.get()),TYPE);}
 private static final class Category implements IRecipeCategory<LegacyRecipeDisplays.Display>{
  final IDrawable bg,icon;Category(IGuiHelper helper){bg=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(SoyuzLauncherContent.ITEM.get()));}
  @Override public RecipeType<LegacyRecipeDisplays.Display> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("recipe.hbm.category.soyuz_construction");}@Override public IDrawable getBackground(){return bg;}@Override public IDrawable getIcon(){return icon;}
  @Override public void setRecipe(IRecipeLayoutBuilder builder,LegacyRecipeDisplays.Display recipe,IFocusGroup focus){var category=SoyuzConstruction.category();int[][] points=LegacyRecipeDisplays.inputCoordinates(category,recipe.inputs().size());for(int i=0;i<points.length;i++)builder.addSlot(RecipeIngredientRole.INPUT,points[i][0],points[i][1]).addItemStacks(recipe.inputs().get(i).alternatives());builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(SoyuzLauncherContent.STRUCTURE_ITEM.get()));builder.addSlot(RecipeIngredientRole.OUTPUT,102,24).addItemStack(new ItemStack(SoyuzLauncherContent.ITEM.get()));}
  @Override public void draw(LegacyRecipeDisplays.Display recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){LegacyDisplayDrawing.background(graphics,SoyuzConstruction.category(),recipe,0,0);}
 }
}
