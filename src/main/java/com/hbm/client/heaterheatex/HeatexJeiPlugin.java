package com.hbm.client.heaterheatex;
import com.hbm.machine.heaterheatex.HeatexContent;
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
@JeiPlugin
public final class HeatexJeiPlugin implements IModPlugin {
 static final RecipeType<HeatexViews.View> TYPE=RecipeType.create("hbm","heater_heatex",HeatexViews.View.class);@Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","heater_heatex_jei");}@Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}@Override public void registerRecipes(IRecipeRegistration event){event.addRecipes(TYPE,HeatexViews.recipes());}@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,HeatexContent.ITEM.get());}
 static final class Category implements IRecipeCategory<HeatexViews.View>{final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(HeatexContent.ITEM.get()));}@Override public RecipeType<HeatexViews.View> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.heater_heatex");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}@Override public void setRecipe(IRecipeLayoutBuilder builder,HeatexViews.View recipe,IFocusGroup focus){builder.addSlot(RecipeIngredientRole.INPUT,30,24).addItemStack(recipe.hot());builder.addSlot(RecipeIngredientRole.CATALYST,75,42).addItemStack(new ItemStack(HeatexContent.ITEM.get()));builder.addSlot(RecipeIngredientRole.OUTPUT,120,24).addItemStack(recipe.cold());}@Override public void draw(HeatexViews.View recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){HeatexViews.draw(graphics,0,0,recipe);}}
}
