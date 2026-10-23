package com.hbm.machine.ashpit.client;
import com.hbm.machine.ashpit.AshpitContent;
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
public final class AshpitJeiPlugin implements IModPlugin {
 static final RecipeType<AshpitViews.View> TYPE=RecipeType.create("hbm","ashpit",AshpitViews.View.class);@Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","ashpit_jei");}@Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}@Override public void registerRecipes(IRecipeRegistration event){event.addRecipes(TYPE,AshpitViews.recipes());}@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,AshpitContent.ITEM.get());}
 static final class Category implements IRecipeCategory<AshpitViews.View>{final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(AshpitContent.ITEM.get()));}@Override public RecipeType<AshpitViews.View> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.machine_ashpit");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}@Override public void setRecipe(IRecipeLayoutBuilder builder,AshpitViews.View recipe,IFocusGroup focus){builder.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStacks(recipe.machines());builder.addSlot(RecipeIngredientRole.INPUT,30,24).addItemStacks(recipe.input());builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(AshpitContent.ITEM.get()));builder.addSlot(RecipeIngredientRole.OUTPUT,102,24).addItemStack(recipe.ash());}@Override public void draw(AshpitViews.View recipe,IRecipeSlotsView slots,GuiGraphics g,double x,double y){AshpitViews.draw(g,0,0);}}
}
