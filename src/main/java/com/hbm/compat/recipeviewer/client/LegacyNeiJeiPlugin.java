package com.hbm.compat.recipeviewer.client;

import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays.Display;
import java.util.*;
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

/** Optional actual JEI categories for source processing graphs whose machines are independently pending. */
@JeiPlugin
public final class LegacyNeiJeiPlugin implements IModPlugin {
    private static final Map<String,RecipeType<Display>> TYPES=new LinkedHashMap<>();
    private static RecipeType<Display> type(String id) { return TYPES.computeIfAbsent(id,key->RecipeType.create("hbm","legacy_nei/"+key,Display.class)); }
    @Override public ResourceLocation getPluginUid() { return ResourceLocation.fromNamespaceAndPath("hbm","legacy_nei_displays"); }
    @Override public void registerCategories(IRecipeCategoryRegistration registration) {
        for(var category:LegacyRecipeDisplays.categories()) registration.addRecipeCategories(new Category(category,registration.getJeiHelpers().getGuiHelper()));
    }
    @Override public void registerRecipes(IRecipeRegistration registration) { for(var category:LegacyRecipeDisplays.categories()) registration.addRecipes(type(category.id()),LegacyRecipeDisplays.recipes(category.id())); }
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        for(var category:LegacyRecipeDisplays.categories()) for(var machine:category.machines()) registration.addRecipeCatalyst(machine,type(category.id()));
    }
    private static final class Category implements IRecipeCategory<Display> {
        private final LegacyRecipeDisplays.Category category;
        private final IDrawable blank,icon;
        Category(LegacyRecipeDisplays.Category category,IGuiHelper helper) { this.category=category; blank=helper.createBlankDrawable(category.width(),category.height()); icon=helper.createDrawableItemStack(LegacyRecipeDisplays.icon(category)); }
        @Override public RecipeType<Display> getRecipeType() { return type(category.id()); }
        @Override public Component getTitle() { return Component.translatable("recipe.hbm.category." + category.id()); }
        @Override public IDrawable getBackground() { return blank; }
        @Override public IDrawable getIcon() { return icon; }
        @Override public void setRecipe(IRecipeLayoutBuilder builder,Display recipe,IFocusGroup focuses) {
            int[][] inputs=LegacyRecipeDisplays.inputCoordinates(category,recipe.inputs().size()),outputs=LegacyRecipeDisplays.outputCoordinates(category,recipe);
            for(int i=0;i<recipe.inputs().size();i++) builder.addSlot(RecipeIngredientRole.INPUT,inputs[i][0],inputs[i][1]).addItemStacks(recipe.inputs().get(i).alternatives()).addTooltipCallback((slot,tooltip)->slot.getDisplayedItemStack().ifPresent(stack->tooltip.addAll(com.hbm.ore.OreRecipeInputs.tooltip(stack))));
            for(int i=0;i<recipe.outputs().size();i++) if(!recipe.outputs().get(i).alternatives().isEmpty()) {
                var source=recipe.outputs().get(i);
                builder.addSlot(RecipeIngredientRole.OUTPUT,outputs[i][0],outputs[i][1]).addItemStacks(source.alternatives()).addTooltipCallback((slot,tooltip)->slot.getDisplayedItemStack().ifPresent(stack->tooltip.addAll(source.tooltip(stack))));
            }
            if(!category.machines().isEmpty()&&(category.layout().equals("generic")||category.layout().equals("universal"))) builder.addSlot(RecipeIngredientRole.CATALYST,75+LegacyRecipeDisplays.assemblyOffset(category,recipe),31).addItemStacks(category.machines());
            var tools=LegacyDisplayDrawing.catalysts(category);
            if(!tools.isEmpty()) {
                if(category.layout().equals("shredder")) { builder.addSlot(RecipeIngredientRole.CATALYST,84,6).addItemStacks(tools); builder.addSlot(RecipeIngredientRole.CATALYST,84,42).addItemStacks(tools); }
                if(category.layout().equals("alloy")) builder.addSlot(RecipeIngredientRole.CATALYST,3,25).addItemStacks(tools);
            }
        }
        @Override public void draw(Display recipe,IRecipeSlotsView slots,GuiGraphics graphics,double mouseX,double mouseY) {
            LegacyDisplayDrawing.background(graphics,category,recipe,0,0); LegacyDisplayDrawing.extras(graphics,category,recipe,0,0);
        }
    }
}
