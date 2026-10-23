package com.hbm.client.rotaryfurnace;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.machine.rotaryfurnace.*;
import java.util.List;
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
@JeiPlugin
public final class RotaryFurnaceJeiPlugin implements IModPlugin {
 static final RecipeType<RotaryFurnaceRecipe> TYPE=RecipeType.create("hbm","rotary_furnace",RotaryFurnaceRecipe.class);
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:rotary_furnace_jei");}@Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}@Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,RotaryFurnaceRecipes.recipes(level.getRecipeManager()));}@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,RotaryFurnaceContent.ITEM.get());}@Override public void registerGuiHandlers(IGuiHandlerRegistration event){event.addRecipeClickArea(RotaryFurnaceScreen.class,58,19,32,10,TYPE);}
 private static final class Category implements IRecipeCategory<RotaryFurnaceRecipe>{final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(RotaryFurnaceContent.ITEM.get()));}@Override public RecipeType<RotaryFurnaceRecipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.machineRotaryFurnace");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}@Override public void setRecipe(IRecipeLayoutBuilder builder,RotaryFurnaceRecipe recipe,IFocusGroup focuses){var display=RotaryFurnaceViews.drawing(recipe);var in=RotaryFurnaceViews.inputs(recipe);var out=RotaryFurnaceViews.outputs(recipe);var ip=LegacyRecipeDisplays.inputCoordinates(RotaryFurnaceViews.CATEGORY,in.size());var op=LegacyRecipeDisplays.outputCoordinates(RotaryFurnaceViews.CATEGORY,display);for(int i=0;i<in.size();i++)builder.addSlot(RecipeIngredientRole.INPUT,ip[i][0],ip[i][1]).addItemStacks(in.get(i)).addTooltipCallback((slot,tooltip)->slot.getDisplayedItemStack().ifPresent(stack->tooltip.addAll(com.hbm.ore.OreRecipeInputs.tooltip(stack))));for(int i=0;i<out.size();i++)builder.addSlot(RecipeIngredientRole.OUTPUT,op[i][0],op[i][1]).addItemStacks(out.get(i));builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStacks(List.of(new ItemStack(RotaryFurnaceContent.ITEM.get())));}@Override public void draw(RotaryFurnaceRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){RotaryFurnaceViews.draw(graphics,recipe,0,0);}}
}
