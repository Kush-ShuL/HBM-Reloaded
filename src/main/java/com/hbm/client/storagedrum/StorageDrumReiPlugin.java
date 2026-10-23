package com.hbm.client.storagedrum;

import java.util.List;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.network.chat.Component;

@REIPluginClient
public final class StorageDrumReiPlugin implements REIClientPlugin {
    private static final CategoryIdentifier<Display> TYPE = CategoryIdentifier.of("hbm", "rbmk_waste_decay");
    @Override public void registerCategories(CategoryRegistry event) { event.add(new Category()); event.addWorkstations(TYPE, EntryStacks.of(StorageDrumViews.catalyst())); }
    @Override public void registerDisplays(DisplayRegistry event) { for (var recipe : StorageDrumViews.recipes()) event.add(new Display(recipe)); }
    private static final class Display extends BasicDisplay {
        Display(StorageDrumViews.Row recipe) { super(List.of(EntryIngredient.of(EntryStacks.of(recipe.input()))), List.of(EntryIngredient.of(EntryStacks.of(recipe.output())))); }
        @Override public CategoryIdentifier<Display> getCategoryIdentifier() { return TYPE; }
    }
    private static final class Category implements DisplayCategory<Display> {
        @Override public CategoryIdentifier<Display> getCategoryIdentifier() { return TYPE; }
        @Override public Component getTitle() { return Component.translatable("jei.hbm.storage_drum_decay"); }
        @Override public Renderer getIcon() { return EntryStacks.of(StorageDrumViews.catalyst()); }
        @Override public int getDisplayWidth(Display display) { return 166; }
        @Override public int getDisplayHeight() { return 65; }
        @Override public List<Widget> setupDisplay(Display display, Rectangle bounds) {
            return List.of(
                Widgets.createDrawableWidget((graphics, x, y, partial) -> StorageDrumViews.draw(graphics, bounds.x, bounds.y)),
                Widgets.createSlot(new Point(bounds.x + 48, bounds.y + 24)).entries(display.getInputEntries().getFirst()).disableBackground().markInput(),
                Widgets.createSlot(new Point(bounds.x + 102, bounds.y + 24)).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput(),
                Widgets.createSlot(new Point(bounds.x + 75, bounds.y + 31)).entry(EntryStacks.of(StorageDrumViews.catalyst())).disableBackground()
            );
        }
    }
}
