package com.hbm.client.generator.diesel;
import com.hbm.machine.generator.diesel.DieselContent;
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
public final class DieselJeiPlugin implements IModPlugin {
    static final RecipeType<DieselFuelViews.View> TYPE=RecipeType.create("hbm","diesel_fuels",DieselFuelViews.View.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:diesel_jei");}@Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}@Override public void registerRecipes(IRecipeRegistration event){event.addRecipes(TYPE,DieselFuelViews.all());}@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,DieselContent.ITEM.get());}
    private static final class Category implements IRecipeCategory<DieselFuelViews.View> {final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(DieselContent.ITEM.get()));}@Override public RecipeType<DieselFuelViews.View> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.machine_diesel");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}@Override public void setRecipe(IRecipeLayoutBuilder builder,DieselFuelViews.View recipe,IFocusGroup focus){builder.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStack(recipe.fuel());builder.addSlot(RecipeIngredientRole.CATALYST,102,24).addItemStack(new ItemStack(DieselContent.ITEM.get()));}@Override public void draw(DieselFuelViews.View recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){DieselFuelViews.draw(graphics,0,0,recipe.energy());}}
}
