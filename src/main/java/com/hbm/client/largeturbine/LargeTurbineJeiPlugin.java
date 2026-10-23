package com.hbm.client.largeturbine;
import com.hbm.machine.largeturbine.LargeTurbineContent;
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
@JeiPlugin public final class LargeTurbineJeiPlugin implements IModPlugin {
 private static final RecipeType<LargeTurbineViews.View> TYPE=RecipeType.create("hbm","large_turbine_legacy",LargeTurbineViews.View.class);
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:large_turbine_legacy_jei");}@Override public void registerCategories(IRecipeCategoryRegistration e){e.addRecipeCategories(new Category(e.getJeiHelpers().getGuiHelper()));}@Override public void registerRecipes(IRecipeRegistration e){e.addRecipes(TYPE,LargeTurbineViews.recipes());}@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration e){e.addRecipeCatalysts(TYPE,LargeTurbineContent.ITEM.get());}
 private static final class Category implements IRecipeCategory<LargeTurbineViews.View>{private final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(LargeTurbineContent.ITEM.get()));}@Override public RecipeType<LargeTurbineViews.View> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.machine_large_turbine");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}@Override public void setRecipe(IRecipeLayoutBuilder b,LargeTurbineViews.View r,IFocusGroup focus){b.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStack(r.input());b.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(LargeTurbineContent.ITEM.get()));b.addSlot(RecipeIngredientRole.OUTPUT,102,24).addItemStack(r.output());}@Override public void draw(LargeTurbineViews.View r,IRecipeSlotsView slots,GuiGraphics g,double x,double y){LargeTurbineViews.draw(g,0,0,r.energy());}}
}
