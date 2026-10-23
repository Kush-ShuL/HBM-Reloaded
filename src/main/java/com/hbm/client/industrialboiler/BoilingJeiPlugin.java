package com.hbm.client.industrialboiler;
import com.hbm.machine.industrialboiler.IndustrialBoilerContent;
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
public final class BoilingJeiPlugin implements IModPlugin {
 static final RecipeType<BoilingViews.View> TYPE=RecipeType.create("hbm","boiling",BoilingViews.View.class);@Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","boiling_jei");}@Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}@Override public void registerRecipes(IRecipeRegistration event){event.addRecipes(TYPE,BoilingViews.recipes());}@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){for(var machine:BoilingViews.machines())event.addRecipeCatalysts(TYPE,machine.getItem());}
 static final class Category implements IRecipeCategory<BoilingViews.View>{final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(IndustrialBoilerContent.ITEM.get()));}@Override public RecipeType<BoilingViews.View> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("tile.machine_boiler.name");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}@Override public void setRecipe(IRecipeLayoutBuilder builder,BoilingViews.View recipe,IFocusGroup focus){builder.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStack(recipe.input());builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStacks(BoilingViews.machines());builder.addSlot(RecipeIngredientRole.OUTPUT,102,24).addItemStack(recipe.output());}@Override public void draw(BoilingViews.View recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){BoilingViews.draw(graphics,0,0);}}
}
