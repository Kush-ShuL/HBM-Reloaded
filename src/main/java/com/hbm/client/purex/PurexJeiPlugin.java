package com.hbm.client.purex;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.machine.purex.*;
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
public final class PurexJeiPlugin implements IModPlugin {
    static final RecipeType<PurexRecipe> TYPE=RecipeType.create("hbm","purex",PurexRecipe.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","purex_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,PurexRecipeConfig.recipes(level.getRecipeManager()).stream().filter(PurexViews::visible).toList());}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,PurexContent.ITEM.get());}
    private static final class Category implements IRecipeCategory<PurexRecipe>{
        final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(PurexContent.ITEM.get()));}
        @Override public RecipeType<PurexRecipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.machinePUREX");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,PurexRecipe recipe,IFocusGroup focuses){var display=PurexViews.drawing(recipe);var in=PurexViews.inputs(recipe);var out=PurexViews.outputs(recipe);var ip=LegacyRecipeDisplays.inputCoordinates(PurexViews.CATEGORY,in.size());var op=LegacyRecipeDisplays.outputCoordinates(PurexViews.CATEGORY,display);for(int i=0;i<in.size();i++)builder.addSlot(RecipeIngredientRole.INPUT,ip[i][0],ip[i][1]).addItemStacks(in.get(i));for(int i=0;i<out.size();i++){int lane=i;builder.addSlot(RecipeIngredientRole.OUTPUT,op[i][0],op[i][1]).addItemStacks(out.get(i)).addTooltipCallback((slot,tooltip)->slot.getDisplayedItemStack().ifPresent(stack->tooltip.addAll(PurexViews.outputTooltip(recipe,lane,stack))));}int offset=PurexViews.offset(recipe);if(recipe.isPooled())builder.addSlot(RecipeIngredientRole.CATALYST,75+offset,10).addItemStacks(PurexViews.blueprints(recipe));builder.addSlot(RecipeIngredientRole.CATALYST,75+offset,recipe.isPooled()?38:31).addItemStack(new ItemStack(PurexContent.ITEM.get()));}
        @Override public void draw(PurexRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){PurexViews.draw(graphics,recipe,0,0);}
    }
}
