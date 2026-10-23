package com.hbm.client.radgen;

import com.hbm.machine.radgen.RadGenFuels;
import mezz.jei.api.*;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public final class RadGenJeiPlugin implements IModPlugin {
    private static final RecipeType<RadGenFuels.Fuel> TYPE = RecipeType.create("hbm", "radgen_fuel", RadGenFuels.Fuel.class);
    @Override public ResourceLocation getPluginUid() { return ResourceLocation.parse("hbm:radgen"); }
    @Override public void registerCategories(IRecipeCategoryRegistration event) { event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper())); }
    @Override public void registerRecipes(IRecipeRegistration event) { event.addRecipes(TYPE, RadGenFuels.fuels()); }
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event) { event.addRecipeCatalyst(RadGenViews.catalyst(), TYPE); }
    private static final class Category implements IRecipeCategory<RadGenFuels.Fuel> {
        private final IDrawable background, icon;
        Category(IGuiHelper helper) { background = helper.createBlankDrawable(166, 65); icon = helper.createDrawableItemStack(RadGenViews.catalyst()); }
        @Override public RecipeType<RadGenFuels.Fuel> getRecipeType() { return TYPE; }
        @Override public Component getTitle() { return Component.translatable("jei.hbm.radgen"); }
        @Override public IDrawable getBackground() { return background; }
        @Override public IDrawable getIcon() { return icon; }
        @Override public void setRecipe(IRecipeLayoutBuilder builder, RadGenFuels.Fuel fuel, IFocusGroup focus) { builder.addSlot(RecipeIngredientRole.INPUT, 48, 17).addItemStack(fuel.inputStack()); if (!fuel.outputStack().isEmpty()) builder.addSlot(RecipeIngredientRole.OUTPUT, 102, 17).addItemStack(fuel.outputStack()); }
        @Override public void draw(RadGenFuels.Fuel fuel, IRecipeSlotsView slots, GuiGraphics graphics, double x, double y) { RadGenViews.draw(fuel, graphics, 0, 0); }
    }
}
