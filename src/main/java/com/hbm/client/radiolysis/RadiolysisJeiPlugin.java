package com.hbm.client.radiolysis;
import com.hbm.machine.radiolysis.*;
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
public final class RadiolysisJeiPlugin implements IModPlugin {
    static final RecipeType<RadiolysisRecipe> TYPE=RecipeType.create("hbm","radiolysis",RadiolysisRecipe.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","radiolysis_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,RadiolysisRecipeConfig.recipes(level.getRecipeManager()));}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,RadiolysisContent.ITEM.get());}
    @Override public void registerGuiHandlers(IGuiHandlerRegistration event){event.addRecipeClickArea(RadiolysisScreen.class,66,25,25,14,TYPE);}
    private static final class Category implements IRecipeCategory<RadiolysisRecipe>{final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(RadiolysisContent.ITEM.get()));}@Override public RecipeType<RadiolysisRecipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("jei.hbm.radiolysis");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,RadiolysisRecipe recipe,IFocusGroup focus){builder.addSlot(RecipeIngredientRole.INPUT,34,25).addItemStack(RadiolysisViews.input(recipe));var out=RadiolysisViews.outputs(recipe);for(int i=0;i<out.size();i++)if(!out.get(i).isEmpty())builder.addSlot(RecipeIngredientRole.OUTPUT,RadiolysisViews.OUTPUT[i][0],RadiolysisViews.OUTPUT[i][1]).addItemStack(out.get(i));}
        @Override public void draw(RadiolysisRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){RadiolysisViews.draw(graphics,0,0);}
    }
}
