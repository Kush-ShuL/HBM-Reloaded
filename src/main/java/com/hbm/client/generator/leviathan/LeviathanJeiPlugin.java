package com.hbm.client.generator.leviathan;
import com.hbm.machine.generator.leviathan.LeviathanContent;
import mezz.jei.api.*;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
@JeiPlugin
public final class LeviathanJeiPlugin implements IModPlugin {
    private static final RecipeType<LeviathanViews.View> TYPE=RecipeType.create("hbm","leviathan",LeviathanViews.View.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:leviathan_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){event.addRecipes(TYPE,LeviathanViews.recipes());}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,LeviathanContent.ITEM.get());}
    private static final class Category implements IRecipeCategory<LeviathanViews.View> {
        private final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(LeviathanContent.ITEM.get()));}
        @Override public RecipeType<LeviathanViews.View> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.machine_chungus");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,LeviathanViews.View recipe,IFocusGroup focus){builder.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStack(recipe.input());builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(LeviathanContent.ITEM.get()));builder.addSlot(RecipeIngredientRole.OUTPUT,102,24).addItemStack(recipe.output());}
        @Override public void draw(LeviathanViews.View recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){LeviathanViews.draw(graphics,0,0,recipe.energy());}
    }
}
