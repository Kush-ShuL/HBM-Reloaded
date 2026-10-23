package com.hbm.client.custommachine;
import com.hbm.machine.custommachine.*;
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
@JeiPlugin
public final class CustomMachineJeiPlugin implements IModPlugin {
 static final RecipeType<CustomMachineViews.Display> TYPE=RecipeType.create("hbm","custom_machine",CustomMachineViews.Display.class);
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:custom_machine_jei");}
 @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
 @Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,CustomMachineViews.all(level.getRecipeManager()));}
 @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){for(var stack:CustomMachineContent.controllers())event.addRecipeCatalyst(stack,TYPE);}
 private static final class Category implements IRecipeCategory<CustomMachineViews.Display>{final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,87);icon=helper.createDrawableItemStack(CustomMachineContent.controller(100));}@Override public RecipeType<CustomMachineViews.Display> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("tile.custom_machine.name");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}@Override public void setRecipe(IRecipeLayoutBuilder builder,CustomMachineViews.Display display,IFocusGroup focus){var in=CustomMachineViews.inputs(display);var out=CustomMachineViews.outputs(display);var ip=CustomMachineViews.coordinates(display,false);var op=CustomMachineViews.coordinates(display,true);for(int i=0;i<in.size();i++)builder.addSlot(RecipeIngredientRole.INPUT,ip[i][0],ip[i][1]).addItemStacks(in.get(i));for(int i=0;i<out.size();i++){int lane=i;builder.addSlot(RecipeIngredientRole.OUTPUT,op[i][0],op[i][1]).addItemStacks(out.get(i)).addTooltipCallback((slot,tooltip)->tooltip.addAll(CustomMachineViews.tooltip(display,lane)));}builder.addSlot(RecipeIngredientRole.CATALYST,75,42).addItemStack(display.machine());}@Override public void draw(CustomMachineViews.Display display,IRecipeSlotsView slots,GuiGraphics g,double x,double y){CustomMachineViews.draw(g,display,0,0);}}
}
