package com.hbm.client.reactor.rbmk.thermal;
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
@JeiPlugin
public final class RbmkThermalJeiPlugin implements IModPlugin{
    static final RecipeType<RbmkThermalViews.View> COOLER=RecipeType.create("hbm","rbmk_cooler",RbmkThermalViews.View.class),HEATER=RecipeType.create("hbm","rbmk_heater",RbmkThermalViews.View.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:rbmk_thermal_jei");}@Override public void registerCategories(IRecipeCategoryRegistration event){var helper=event.getJeiHelpers().getGuiHelper();event.addRecipeCategories(new Category(helper,true),new Category(helper,false));}@Override public void registerRecipes(IRecipeRegistration event){event.addRecipes(COOLER,RbmkThermalViews.recipes(true));event.addRecipes(HEATER,RbmkThermalViews.recipes(false));}@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(COOLER,RbmkThermalViews.machine(true));event.addRecipeCatalysts(HEATER,RbmkThermalViews.machine(false));}
    private static final class Category implements IRecipeCategory<RbmkThermalViews.View>{final boolean cooler;final IDrawable background,icon;Category(IGuiHelper helper,boolean cooler){this.cooler=cooler;background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(RbmkThermalViews.machine(cooler));}@Override public RecipeType<RbmkThermalViews.View> getRecipeType(){return cooler?COOLER:HEATER;}@Override public Component getTitle(){return RbmkThermalViews.title(cooler);}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}@Override public void setRecipe(IRecipeLayoutBuilder builder,RbmkThermalViews.View recipe,IFocusGroup focuses){builder.addSlot(RecipeIngredientRole.INPUT,30,24).addItemStack(recipe.input());builder.addSlot(RecipeIngredientRole.CATALYST,75,42).addItemStack(RbmkThermalViews.machine(cooler));builder.addSlot(RecipeIngredientRole.OUTPUT,120,24).addItemStack(recipe.output());}@Override public void draw(RbmkThermalViews.View recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){RbmkThermalViews.draw(graphics,0,0,recipe);}}
}
