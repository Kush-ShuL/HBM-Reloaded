package com.hbm.client.electrolyser;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.electrolyser.*;
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
/** Both original NEI categories, electrode/sand metadata and genuine machine catalyst. */
@JeiPlugin
public final class ElectrolyserJeiPlugin implements IModPlugin {
 static final RecipeType<ElectrolyserViews.View> FLUID=RecipeType.create("hbm","electrolyser_fluid",ElectrolyserViews.View.class),METAL=RecipeType.create("hbm","electrolyser_metal",ElectrolyserViews.View.class);
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:electrolyser_jei");}
 @Override public void registerCategories(IRecipeCategoryRegistration event){var helper=event.getJeiHelpers().getGuiHelper();event.addRecipeCategories(new Category(helper,false),new Category(helper,true));}
 @Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null){event.addRecipes(FLUID,ElectrolyserViews.recipes(level.getRecipeManager(),false));event.addRecipes(METAL,ElectrolyserViews.recipes(level.getRecipeManager(),true));}}
 @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(FLUID,ElectrolyserContent.ITEM.get());event.addRecipeCatalysts(METAL,ElectrolyserContent.ITEM.get());}
 static final class Category implements IRecipeCategory<ElectrolyserViews.View>{
  final boolean metal;final IDrawable background,icon;Category(IGuiHelper helper,boolean metal){this.metal=metal;background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(ElectrolyserContent.ITEM.get()));}
  @Override public RecipeType<ElectrolyserViews.View> getRecipeType(){return metal?METAL:FLUID;}@Override public Component getTitle(){return Component.translatable(metal?"gui.hbm.electrolyser.metal":"gui.hbm.electrolyser.fluid");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
  @Override public void setRecipe(IRecipeLayoutBuilder builder,ElectrolyserViews.View view,IFocusGroup focuses){var in=ElectrolyserViews.inputCoords(view);for(int i=0;i<in.length;i++)builder.addSlot(RecipeIngredientRole.INPUT,in[i][0],in[i][1]).addItemStacks(view.inputs().get(i));var out=ElectrolyserViews.outputCoords(view);for(int i=0;i<view.outputs().size();i++)builder.addSlot(RecipeIngredientRole.OUTPUT,out[i][0],out[i][1]).addItemStack(view.outputs().get(i));builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(ElectrolyserContent.ITEM.get()));}
  @Override public void draw(ElectrolyserViews.View view,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){ElectrolyserViews.draw(graphics,view,0,0);}
 }
}
