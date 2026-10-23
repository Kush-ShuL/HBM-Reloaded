package com.hbm.client.storagedrum;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public final class StorageDrumJeiPlugin implements IModPlugin {
    static final RecipeType<StorageDrumViews.Row> TYPE = RecipeType.create("hbm", "rbmk_waste_decay", StorageDrumViews.Row.class);
    @Override public ResourceLocation getPluginUid() { return ResourceLocation.parse("hbm:storage_drum"); }
    @Override public void registerCategories(IRecipeCategoryRegistration event) { event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper())); }
    @Override public void registerRecipes(IRecipeRegistration event) { event.addRecipes(TYPE, StorageDrumViews.recipes()); }
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event) { event.addRecipeCatalyst(StorageDrumViews.catalyst(), TYPE); }
    private static final class Category implements IRecipeCategory<StorageDrumViews.Row> {
        private final IDrawable background, icon;
        Category(IGuiHelper helper) { background = helper.createBlankDrawable(166, 65); icon = helper.createDrawableItemStack(StorageDrumViews.catalyst()); }
        @Override public RecipeType<StorageDrumViews.Row> getRecipeType() { return TYPE; }
        @Override public Component getTitle() { return Component.translatable("jei.hbm.storage_drum_decay"); }
        @Override public IDrawable getBackground() { return background; }
        @Override public IDrawable getIcon() { return icon; }
        @Override public void setRecipe(IRecipeLayoutBuilder builder, StorageDrumViews.Row recipe, IFocusGroup focus) {
            builder.addSlot(RecipeIngredientRole.INPUT, 48, 24).addItemStack(recipe.input());
            builder.addSlot(RecipeIngredientRole.OUTPUT, 102, 24).addItemStack(recipe.output());
            builder.addSlot(RecipeIngredientRole.CATALYST, 75, 31).addItemStack(StorageDrumViews.catalyst());
        }
        @Override public void draw(StorageDrumViews.Row recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) { StorageDrumViews.draw(graphics, 0, 0); }
    }
}
