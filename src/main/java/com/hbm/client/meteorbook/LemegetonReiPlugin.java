package com.hbm.client.meteorbook;

import com.hbm.item.lemegeton.*;
import java.util.*;
import me.shedaniel.math.*;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.*;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.client.registry.transfer.simple.SimpleTransferHandler;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;

@REIPluginClient
public final class LemegetonReiPlugin implements REIClientPlugin {
    public static final CategoryIdentifier<Display> TYPE = CategoryIdentifier.of("hbm", "lemegeton");
    @Override public void registerCategories(CategoryRegistry event) { event.add(new Category()); event.addWorkstations(TYPE, EntryStacks.of(LemegetonContent.BOOK.get())); }
    @Override public void registerDisplays(DisplayRegistry event) { event.registerRecipeFiller(LemegetonRecipe.class, LemegetonContent.TYPE.get(), Display::new); }
    @Override public void registerScreens(ScreenRegistry event) { event.registerContainerClickArea(new Rectangle(73, 32, 26, 24), LemegetonScreen.class, TYPE); }
    @Override public void registerTransferHandlers(TransferHandlerRegistry event) { event.register(SimpleTransferHandler.create(LemegetonMenu.class, TYPE, new SimpleTransferHandler.IntRange(1, 2))); }
    public static final class Display extends BasicDisplay {
        Display(RecipeHolder<LemegetonRecipe> holder) { super(List.of(EntryIngredient.of(holder.value().examples().stream().map(EntryStacks::of).toList())), List.of(EntryIngredient.of(EntryStacks.of(holder.value().result()))), Optional.of(holder.id())); }
        @Override public CategoryIdentifier<Display> getCategoryIdentifier() { return TYPE; }
    }
    private static final class Category implements DisplayCategory<Display> {
        @Override public CategoryIdentifier<Display> getCategoryIdentifier() { return TYPE; }
        @Override public Component getTitle() { return Component.translatable("recipe.hbm.lemegeton"); }
        @Override public Renderer getIcon() { return EntryStacks.of(LemegetonContent.BOOK.get()); }
        @Override public int getDisplayHeight() { return 65; }
        @Override public int getDisplayWidth(Display display) { return 166; }
        @Override public List<Widget> setupDisplay(Display display, Rectangle bounds) {
            var widgets = new ArrayList<Widget>();
            widgets.add(Widgets.createDrawableWidget((graphics, mx, my, partial) -> { graphics.blit(LemegetonScreen.TEXTURE, bounds.x, bounds.y, 5, 11, 166, 65, 256, 256); graphics.blit(LemegetonScreen.TEXTURE, bounds.x + 2, bounds.y + 11, 0, 166, 162, 42, 256, 256); }));
            widgets.add(Widgets.createSlot(new Point(bounds.x + 44, bounds.y + 24)).entries(display.getInputEntries().getFirst()).disableBackground().markInput());
            widgets.add(Widgets.createSlot(new Point(bounds.x + 102, bounds.y + 24)).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput());
            return widgets;
        }
    }
}
