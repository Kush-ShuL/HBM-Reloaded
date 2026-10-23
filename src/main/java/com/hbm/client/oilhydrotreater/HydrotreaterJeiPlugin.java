package com.hbm.client.oilhydrotreater;
import com.hbm.machine.oilhydrotreater.*;
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
public final class HydrotreaterJeiPlugin implements IModPlugin {
    static final RecipeType<HydrotreaterRecipe> TYPE=RecipeType.create("hbm","hydrotreater",HydrotreaterRecipe.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","hydrotreater_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,HydrotreaterRecipes.recipes(level.getRecipeManager()));}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,HydrotreaterContent.ITEM.get());}
    private static final class Category implements IRecipeCategory<HydrotreaterRecipe>{final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(HydrotreaterContent.ITEM.get()));}@Override public RecipeType<HydrotreaterRecipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.hydrotreater");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,HydrotreaterRecipe recipe,IFocusGroup focus){var in=HydrotreaterViews.inputs(recipe);for(int i=0;i<in.size();i++)builder.addSlot(RecipeIngredientRole.INPUT,HydrotreaterViews.INPUT[i][0],HydrotreaterViews.INPUT[i][1]).addItemStack(in.get(i));var out=HydrotreaterViews.outputs(recipe);for(int i=0;i<out.size();i++)if(!out.get(i).isEmpty())builder.addSlot(RecipeIngredientRole.OUTPUT,HydrotreaterViews.OUTPUT[i][0],HydrotreaterViews.OUTPUT[i][1]).addItemStack(out.get(i));builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(HydrotreaterContent.ITEM.get()));}
        @Override public void draw(HydrotreaterRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){HydrotreaterViews.draw(graphics,0,0);}
    }
}
