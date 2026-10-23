package com.hbm.client.solidifier;
import com.hbm.machine.solidifier.*;
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
public final class SolidifierJeiPlugin implements IModPlugin {
    static final RecipeType<SolidifierRecipe> TYPE=RecipeType.create("hbm","solidification",SolidifierRecipe.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","solidification_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,SolidificationRecipes.recipes(level.getRecipeManager()));}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,SolidifierContent.ITEM.get());}
    private static final class Category implements IRecipeCategory<SolidifierRecipe>{final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(SolidifierContent.ITEM.get()));}@Override public RecipeType<SolidifierRecipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("category.hbm.solidification");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,SolidifierRecipe recipe,IFocusGroup focus){builder.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStack(SolidifierViews.input(recipe));builder.addSlot(RecipeIngredientRole.OUTPUT,102,24).addItemStack(recipe.output());builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(SolidifierContent.ITEM.get()));}
        @Override public void draw(SolidifierRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){SolidifierViews.draw(graphics,0,0);}
    }
}
