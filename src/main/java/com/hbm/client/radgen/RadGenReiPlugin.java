package com.hbm.client.radgen;

import com.hbm.machine.radgen.RadGenFuels;
import java.util.*;
import me.shedaniel.math.*;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.*;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.network.chat.Component;

@REIPluginClient
public final class RadGenReiPlugin implements REIClientPlugin {
    private static final CategoryIdentifier<Display> TYPE = CategoryIdentifier.of("hbm", "radgen_fuel");
    @Override public void registerCategories(CategoryRegistry event) { event.add(new Category()); event.addWorkstations(TYPE, EntryStacks.of(RadGenViews.catalyst())); }
    @Override public void registerDisplays(DisplayRegistry event) { for (var fuel : RadGenFuels.fuels()) event.add(new Display(fuel)); }
    private static final class Display extends BasicDisplay {
        final RadGenFuels.Fuel fuel;
        Display(RadGenFuels.Fuel fuel) { super(List.of(EntryIngredient.of(EntryStacks.of(fuel.inputStack()))), fuel.outputStack().isEmpty() ? List.of() : List.of(EntryIngredient.of(EntryStacks.of(fuel.outputStack())))); this.fuel = fuel; }
        @Override public CategoryIdentifier<Display> getCategoryIdentifier() { return TYPE; }
    }
    private static final class Category implements DisplayCategory<Display> {
        @Override public CategoryIdentifier<Display> getCategoryIdentifier() { return TYPE; }
        @Override public Component getTitle() { return Component.translatable("jei.hbm.radgen"); }
        @Override public Renderer getIcon() { return EntryStacks.of(RadGenViews.catalyst()); }
        @Override public int getDisplayWidth(Display display) { return 166; }
        @Override public int getDisplayHeight() { return 65; }
        @Override public List<Widget> setupDisplay(Display display, Rectangle bounds) {
            var result = new ArrayList<Widget>();
            result.add(Widgets.createDrawableWidget((graphics, x, y, partial) -> RadGenViews.draw(display.fuel, graphics, bounds.x, bounds.y)));
            result.add(Widgets.createSlot(new Point(bounds.x + 48, bounds.y + 17)).entries(display.getInputEntries().getFirst()).disableBackground().markInput());
            if (!display.fuel.outputStack().isEmpty()) result.add(Widgets.createSlot(new Point(bounds.x + 102, bounds.y + 17)).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput());
            return result;
        }
    }
}
