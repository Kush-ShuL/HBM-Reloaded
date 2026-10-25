package com.hbm.client.soyuzlauncher;

import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing;
import com.hbm.machine.soyuzlauncher.SoyuzLauncherContent;
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
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public final class SoyuzConstructionJeiPlugin implements IModPlugin {
    static final RecipeType<SoyuzConstruction.Build> TYPE = RecipeType.create("hbm", "soyuz_construction", SoyuzConstruction.Build.class);
    @Override public ResourceLocation getPluginUid() { return SoyuzLauncherContent.id("soyuz_construction_jei"); }
    @Override public void registerCategories(IRecipeCategoryRegistration event) { event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper())); }
    @Override public void registerRecipes(IRecipeRegistration event) { event.addRecipes(TYPE, SoyuzConstruction.recipes()); }
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event) {
        for (var tool : SoyuzConstruction.tools()) event.addRecipeCatalyst(tool, TYPE);
    }
    private static final class Category implements IRecipeCategory<SoyuzConstruction.Build> {
        private final IDrawable background, icon;
        Category(IGuiHelper helper) {
            background = helper.createBlankDrawable(166, 65);
            icon = helper.createDrawableItemStack(new ItemStack(SoyuzLauncherContent.ITEM.get()));
        }
        @Override public RecipeType<SoyuzConstruction.Build> getRecipeType() { return TYPE; }
        @Override public Component getTitle() { return Component.translatable("recipe.hbm.category.soyuz_construction"); }
        @Override public IDrawable getBackground() { return background; }
        @Override public IDrawable getIcon() { return icon; }
        @Override public void setRecipe(IRecipeLayoutBuilder builder, SoyuzConstruction.Build build, IFocusGroup focus) {
            var recipe = build.display();
            var points = LegacyRecipeDisplays.inputCoordinates(SoyuzConstruction.category(), recipe.inputs().size());
            for (int i = 0; i < points.length; i++) builder.addSlot(RecipeIngredientRole.INPUT, points[i][0], points[i][1]).addItemStacks(recipe.inputs().get(i).alternatives());
            builder.addSlot(RecipeIngredientRole.CATALYST, 75, 31).addItemStack(build.core());
            builder.addSlot(RecipeIngredientRole.OUTPUT, 102, 24).addItemStacks(recipe.outputs().getFirst().alternatives());
        }
        @Override public void draw(SoyuzConstruction.Build build, IRecipeSlotsView slots, GuiGraphics graphics, double x, double y) {
            LegacyDisplayDrawing.background(graphics, SoyuzConstruction.category(), build.display(), 0, 0);
        }
    }
}
