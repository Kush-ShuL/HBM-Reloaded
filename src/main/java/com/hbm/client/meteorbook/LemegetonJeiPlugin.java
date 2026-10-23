package com.hbm.client.meteorbook;

import com.hbm.item.lemegeton.*;
import java.util.Comparator;
import mezz.jei.api.*;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** Native viewer for the actual one-cell book table; never registers these conversions as ordinary crafting. */
@JeiPlugin
public final class LemegetonJeiPlugin implements IModPlugin {
    public static final RecipeType<LemegetonRecipe> TYPE = RecipeType.create("hbm", "lemegeton", LemegetonRecipe.class);
    @Override public ResourceLocation getPluginUid() { return ResourceLocation.parse("hbm:lemegeton224_jei"); }
    @Override public void registerCategories(IRecipeCategoryRegistration event) { event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper())); }
    @Override public void registerRecipes(IRecipeRegistration event) { var level = Minecraft.getInstance().level; if (level != null) event.addRecipes(TYPE, level.getRecipeManager().getAllRecipesFor(LemegetonContent.TYPE.get()).stream().map(holder -> holder.value()).sorted(Comparator.comparingInt(LemegetonRecipe::order)).toList()); }
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event) { event.addRecipeCatalysts(TYPE, LemegetonContent.BOOK.get()); }
    @Override public void registerGuiHandlers(IGuiHandlerRegistration event) { event.addRecipeClickArea(LemegetonScreen.class, 73, 32, 26, 24, TYPE); }
    @Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration event) { event.addRecipeTransferHandler(LemegetonMenu.class, LemegetonContent.MENU.get(), TYPE, 1, 1, 2, 36); }
    private static final class Category implements IRecipeCategory<LemegetonRecipe> {
        private final IDrawable background, icon;
        Category(mezz.jei.api.helpers.IGuiHelper gui) { background = gui.createDrawable(LemegetonScreen.TEXTURE, 5, 11, 166, 65); icon = gui.createDrawableItemStack(new ItemStack(LemegetonContent.BOOK.get())); }
        @Override public RecipeType<LemegetonRecipe> getRecipeType() { return TYPE; }
        @Override public Component getTitle() { return Component.translatable("recipe.hbm.lemegeton"); }
        @Override public IDrawable getBackground() { return background; }
        @Override public IDrawable getIcon() { return icon; }
        @Override public void draw(LemegetonRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mx, double my) { graphics.blit(LemegetonScreen.TEXTURE, 2, 11, 0, 166, 162, 42, 256, 256); }
        @Override public void setRecipe(IRecipeLayoutBuilder layout, LemegetonRecipe recipe, IFocusGroup focuses) { layout.addSlot(RecipeIngredientRole.INPUT, 44, 24).addItemStacks(recipe.examples()); layout.addSlot(RecipeIngredientRole.OUTPUT, 102, 24).addItemStack(recipe.result()); }
    }
}
