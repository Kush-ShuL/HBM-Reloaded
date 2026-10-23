package com.hbm.compat.recipeviewer.client;

import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import java.util.*;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.*;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.network.chat.Component;

/** Uses exactly the same bound source data, amounts and positional layouts as JEI. */
@REIPluginClient
public final class LegacyNeiReiPlugin implements REIClientPlugin {
    private static CategoryIdentifier<Display> id(String category) { return CategoryIdentifier.of("hbm","legacy_nei/"+category); }
    @Override public void registerCategories(CategoryRegistry registration) {
        for(var category:LegacyRecipeDisplays.categories()) {
            registration.add(new Category(category));
            category.machines().forEach(machine->registration.addWorkstations(id(category.id()),EntryStacks.of(machine)));
        }
    }
    @Override public void registerDisplays(DisplayRegistry registration) { for(var category:LegacyRecipeDisplays.categories()) for(var recipe:LegacyRecipeDisplays.recipes(category.id())) registration.add(new Display(recipe)); }
    public static final class Display extends BasicDisplay {
        final LegacyRecipeDisplays.Display recipe;
        Display(LegacyRecipeDisplays.Display recipe) {
            super(recipe.inputs().stream().map(slot->EntryIngredient.of(slot.alternatives().stream().map(stack->EntryStacks.of(stack).tooltipProcessor((entry,tooltip)->tooltip.addAllTexts(com.hbm.ore.OreRecipeInputs.tooltip(entry.getValue())))).toList())).toList(),
                  recipe.outputs().stream().map(slot->EntryIngredient.of(slot.alternatives().stream().map(stack->EntryStacks.of(stack).tooltipProcessor((entry,tooltip)->tooltip.addAllTexts(slot.tooltip(entry.getValue())))).toList())).toList(),Optional.of(recipe.id())); this.recipe=recipe;
        }
        @Override public CategoryIdentifier<Display> getCategoryIdentifier() { return id(recipe.category()); }
    }
    private static final class Category implements DisplayCategory<Display> {
        private final LegacyRecipeDisplays.Category category;
        Category(LegacyRecipeDisplays.Category category) { this.category=category; }
        @Override public CategoryIdentifier<Display> getCategoryIdentifier() { return id(category.id()); }
        @Override public Component getTitle() { return Component.translatable("recipe.hbm.category." + category.id()); }
        @Override public Renderer getIcon() { return EntryStacks.of(LegacyRecipeDisplays.icon(category)); }
        @Override public int getDisplayHeight() { return category.height(); }
        @Override public int getDisplayWidth(Display display) { return category.width(); }
        @Override public List<Widget> setupDisplay(Display display,Rectangle bounds) {
            var recipe=display.recipe; var widgets=new ArrayList<Widget>();
            widgets.add(Widgets.createDrawableWidget((graphics,x,y,partial)->LegacyDisplayDrawing.background(graphics,category,recipe,bounds.x,bounds.y)));
            int[][] inputs=LegacyRecipeDisplays.inputCoordinates(category,recipe.inputs().size()),outputs=LegacyRecipeDisplays.outputCoordinates(category,recipe);
            for(int i=0;i<recipe.inputs().size();i++) widgets.add(Widgets.createSlot(new Point(bounds.x+inputs[i][0],bounds.y+inputs[i][1])).entries(display.getInputEntries().get(i)).disableBackground().markInput());
            for(int i=0;i<recipe.outputs().size();i++) widgets.add(Widgets.createSlot(new Point(bounds.x+outputs[i][0],bounds.y+outputs[i][1])).entries(display.getOutputEntries().get(i)).disableBackground().markOutput());
            if(!category.machines().isEmpty()&&(category.layout().equals("generic")||category.layout().equals("universal"))) widgets.add(Widgets.createSlot(new Point(bounds.x+75+LegacyRecipeDisplays.assemblyOffset(category,recipe),bounds.y+31)).entries(category.machines().stream().map(EntryStacks::of).toList()).disableBackground());
            var tools=LegacyDisplayDrawing.catalysts(category).stream().map(EntryStacks::of).toList();
            if(!tools.isEmpty()) {
                if(category.layout().equals("shredder")) { widgets.add(Widgets.createSlot(new Point(bounds.x+84,bounds.y+6)).entries(tools).disableBackground()); widgets.add(Widgets.createSlot(new Point(bounds.x+84,bounds.y+42)).entries(tools).disableBackground()); }
                if(category.layout().equals("alloy")) widgets.add(Widgets.createSlot(new Point(bounds.x+3,bounds.y+25)).entries(tools).disableBackground());
            }
            widgets.add(Widgets.createDrawableWidget((graphics,x,y,partial)->LegacyDisplayDrawing.extras(graphics,category,recipe,bounds.x,bounds.y)));
            return widgets;
        }
    }
}
