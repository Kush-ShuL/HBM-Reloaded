package com.hbm.client.turbine;
import com.hbm.machine.turbine.IndustrialTurbineContent;
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
public final class TurbineJeiPlugin implements IModPlugin {
    private static final RecipeType<TurbineViews.View> TYPE=RecipeType.create("hbm","steam_turbine",TurbineViews.View.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:steam_turbine_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){event.addRecipes(TYPE,TurbineViews.recipes());}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,IndustrialTurbineContent.ITEM.get());}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(mezz.jei.api.constants.VanillaTypes.ITEM_STACK,com.hbm.machine.turbine.ExpensiveComponentItem.creativeStacks());}
    private static final class Category implements IRecipeCategory<TurbineViews.View> {
        private final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(IndustrialTurbineContent.ITEM.get()));}
        @Override public RecipeType<TurbineViews.View> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.machine_industrial_turbine");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,TurbineViews.View recipe,IFocusGroup focus){builder.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStack(recipe.input());builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(IndustrialTurbineContent.ITEM.get()));builder.addSlot(RecipeIngredientRole.OUTPUT,102,24).addItemStack(recipe.output());}
        @Override public void draw(TurbineViews.View recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){TurbineViews.draw(graphics,0,0,recipe.energy());}
    }
}
