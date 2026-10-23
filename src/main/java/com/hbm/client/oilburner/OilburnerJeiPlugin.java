package com.hbm.client.oilburner;
import com.hbm.machine.oilburner.OilburnerContent;
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
public final class OilburnerJeiPlugin implements IModPlugin {
 static final RecipeType<OilburnerViews.View> TYPE=RecipeType.create("hbm","heater_oilburner",OilburnerViews.View.class);@Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","heater_oilburner_jei");}@Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}@Override public void registerRecipes(IRecipeRegistration event){event.addRecipes(TYPE,OilburnerViews.recipes());}@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,OilburnerContent.ITEM.get());}
 static final class Category implements IRecipeCategory<OilburnerViews.View>{final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(OilburnerContent.ITEM.get()));}@Override public RecipeType<OilburnerViews.View> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.heater_oilburner");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}@Override public void setRecipe(IRecipeLayoutBuilder builder,OilburnerViews.View recipe,IFocusGroup focus){builder.addSlot(RecipeIngredientRole.INPUT,30,24).addItemStack(recipe.fuel());builder.addSlot(RecipeIngredientRole.CATALYST,75,42).addItemStack(new ItemStack(OilburnerContent.ITEM.get()));}@Override public void draw(OilburnerViews.View recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){OilburnerViews.draw(graphics,0,0,recipe);}}
}
