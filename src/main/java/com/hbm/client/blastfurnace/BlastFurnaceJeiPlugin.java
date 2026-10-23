package com.hbm.client.blastfurnace;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.machine.blastfurnace.*;
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
public final class BlastFurnaceJeiPlugin implements IModPlugin {
    static final RecipeType<BlastFurnaceRecipe> TYPE=RecipeType.create("hbm","blast_furnace",BlastFurnaceRecipe.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","blast_furnace_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,BlastFurnaceRecipes.recipes(level.getRecipeManager()).stream().filter(BlastFurnaceViews::visible).toList());}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,BlastFurnaceContent.ITEM.get());}
    private static final class Category implements IRecipeCategory<BlastFurnaceRecipe>{
        final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(BlastFurnaceContent.ITEM.get()));}
        @Override public RecipeType<BlastFurnaceRecipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.blastFurnace");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,BlastFurnaceRecipe recipe,IFocusGroup focuses){var display=BlastFurnaceViews.drawing(recipe);var in=BlastFurnaceViews.inputs(recipe);var out=BlastFurnaceViews.outputs(recipe);var ip=LegacyRecipeDisplays.inputCoordinates(BlastFurnaceViews.CATEGORY,in.size());var op=LegacyRecipeDisplays.outputCoordinates(BlastFurnaceViews.CATEGORY,display);for(int i=0;i<in.size();i++)builder.addSlot(RecipeIngredientRole.INPUT,ip[i][0],ip[i][1]).addItemStacks(in.get(i)).addTooltipCallback((slot,tooltip)->slot.getDisplayedItemStack().ifPresent(stack->tooltip.addAll(com.hbm.ore.OreRecipeInputs.tooltip(stack))));for(int i=0;i<out.size();i++){int lane=i;builder.addSlot(RecipeIngredientRole.OUTPUT,op[i][0],op[i][1]).addItemStacks(out.get(i)).addTooltipCallback((slot,tooltip)->slot.getDisplayedItemStack().ifPresent(stack->tooltip.addAll(BlastFurnaceViews.outputTooltip(recipe,lane,stack))));}int offset=BlastFurnaceViews.offset(recipe);if(recipe.isPooled())builder.addSlot(RecipeIngredientRole.CATALYST,75+offset,10).addItemStacks(BlastFurnaceViews.blueprints(recipe));builder.addSlot(RecipeIngredientRole.CATALYST,75+offset,recipe.isPooled()?38:31).addItemStack(new ItemStack(BlastFurnaceContent.ITEM.get()));}
        @Override public void draw(BlastFurnaceRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){BlastFurnaceViews.draw(graphics,recipe,0,0);}
    }
}
