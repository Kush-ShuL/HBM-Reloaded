package com.hbm.client.oilliquefactor;
import com.hbm.machine.oilliquefactor.*;
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
public final class LiquefactorJeiPlugin implements IModPlugin {
    static final RecipeType<LiquefactorRecipe> TYPE=RecipeType.create("hbm","oil_liquefaction",LiquefactorRecipe.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","oil_liquefaction_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,LiquefactionRecipes.recipes(level.getRecipeManager()));}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,LiquefactorContent.ITEM.get());}
    private static final class Category implements IRecipeCategory<LiquefactorRecipe>{final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(LiquefactorContent.ITEM.get()));}@Override public RecipeType<LiquefactorRecipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("category.hbm.liquefaction");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,LiquefactorRecipe recipe,IFocusGroup focus){builder.addSlot(RecipeIngredientRole.INPUT,48,24).addIngredients(recipe.input().ingredient());builder.addSlot(RecipeIngredientRole.OUTPUT,102,24).addItemStack(LiquefactorViews.output(recipe));builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(LiquefactorContent.ITEM.get()));}
        @Override public void draw(LiquefactorRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){LiquefactorViews.draw(graphics,0,0);}
    }
}
