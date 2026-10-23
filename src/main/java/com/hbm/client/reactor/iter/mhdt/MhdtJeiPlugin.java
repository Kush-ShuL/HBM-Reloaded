package com.hbm.client.reactor.iter.mhdt;
import com.hbm.machine.reactor.iter.mhdt.MhdtContent;
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
public final class MhdtJeiPlugin implements IModPlugin {
    private static final RecipeType<MhdtViews.View> TYPE=RecipeType.create("hbm","fusion_mhdt",MhdtViews.View.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:fusion_mhdt_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){event.addRecipes(TYPE,MhdtViews.recipes());}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,MhdtContent.ITEM.get());}
    private static final class Category implements IRecipeCategory<MhdtViews.View> {
        private final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(MhdtContent.ITEM.get()));}
        @Override public RecipeType<MhdtViews.View> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.fusion_mhdt");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,MhdtViews.View recipe,IFocusGroup focus){builder.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStack(recipe.input());builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(MhdtContent.ITEM.get()));builder.addSlot(RecipeIngredientRole.OUTPUT,102,24).addItemStack(recipe.output());}
        @Override public void draw(MhdtViews.View recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){MhdtViews.draw(graphics,0,0,recipe.energy());}
    }
}
