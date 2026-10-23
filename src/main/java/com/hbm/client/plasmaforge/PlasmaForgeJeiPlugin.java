package com.hbm.client.plasmaforge;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.machine.plasmaforge.*;
import com.hbm.recipe.generic.*;
import java.util.*;
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
public final class PlasmaForgeJeiPlugin implements IModPlugin {
    static final RecipeType<PlasmaForgeRecipe> TYPE=RecipeType.create("hbm","plasma_forge",PlasmaForgeRecipe.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","plasma_forge_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,PlasmaForgeRecipeConfig.recipes(level.getRecipeManager()).stream().filter(PlasmaForgeViews::visible).toList());}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,PlasmaForgeContent.ITEM.get());}
    private static final class Category implements IRecipeCategory<PlasmaForgeRecipe>{
        final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(PlasmaForgeContent.ITEM.get()));}
        @Override public RecipeType<PlasmaForgeRecipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.machinePlasmaForge");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,PlasmaForgeRecipe recipe,IFocusGroup focuses){var display=PlasmaForgeViews.drawing(recipe);var in=PlasmaForgeViews.inputs(recipe);var out=PlasmaForgeViews.outputs(recipe);var ip=LegacyRecipeDisplays.inputCoordinates(PlasmaForgeViews.CATEGORY,in.size());var op=LegacyRecipeDisplays.outputCoordinates(PlasmaForgeViews.CATEGORY,display);for(int i=0;i<in.size();i++)builder.addSlot(RecipeIngredientRole.INPUT,ip[i][0],ip[i][1]).addItemStacks(in.get(i)).addTooltipCallback((slot,tooltip)->slot.getDisplayedItemStack().ifPresent(stack->tooltip.addAll(com.hbm.ore.OreRecipeInputs.tooltip(stack))));for(int i=0;i<out.size();i++){int lane=i;builder.addSlot(RecipeIngredientRole.OUTPUT,op[i][0],op[i][1]).addItemStacks(out.get(i)).addTooltipCallback((slot,tooltip)->slot.getDisplayedItemStack().ifPresent(stack->tooltip.addAll(PlasmaForgeViews.outputTooltip(recipe,lane,stack))));}int offset=PlasmaForgeViews.offset(recipe);builder.addSlot(RecipeIngredientRole.CATALYST,75+offset,31).addItemStack(new ItemStack(PlasmaForgeContent.ITEM.get()));}
        @Override public void draw(PlasmaForgeRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){PlasmaForgeViews.draw(graphics,recipe,0,0);}
    }
}
