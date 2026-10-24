package com.hbm.client.meteorbook;

import com.hbm.item.meteorbook.*;
import java.util.*;
import me.shedaniel.math.*;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.*;
import me.shedaniel.rei.api.client.registry.entry.*;
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
public final class BlackBookReiPlugin implements REIClientPlugin {
    public static final CategoryIdentifier<Display> TYPE = CategoryIdentifier.of("hbm", "book_of_boxcars");
    @Override public void registerEntries(EntryRegistry registration) {
        MeteorBookContent.printingStamps().forEach(stack -> registration.addEntry(EntryStacks.of(stack)));
        if (com.hbm.LegacyStartup.polaroidId != 11) registration.removeEntry(EntryStacks.of(MeteorBookContent.BOOK.get()));
    }
    @Override public void registerCategories(CategoryRegistry registration) { registration.add(new Category()); registration.addWorkstations(TYPE, EntryStacks.of(MeteorBookContent.BOOK.get())); }
    @Override public void registerDisplays(DisplayRegistry registration) { registration.registerRecipeFiller(MagicBookRecipe.class, MeteorBookContent.TYPE.get(), Display::new); }
    @Override public void registerTransferHandlers(TransferHandlerRegistry registration) { registration.register(SimpleTransferHandler.create(BlackBookMenu.class, TYPE, new SimpleTransferHandler.IntRange(1, 5))); }
    public static final class Display extends BasicDisplay {
        Display(RecipeHolder<MagicBookRecipe> holder) {
            super(holder.value().inputs().stream().map(ingredient -> EntryIngredient.of(ingredient.examples().stream().map(EntryStacks::of).toList())).toList(), List.of(EntryIngredient.of(EntryStacks.of(holder.value().result()))), Optional.of(holder.id()));
        }
        @Override public CategoryIdentifier<Display> getCategoryIdentifier() { return TYPE; }
    }
    private static final class Category implements DisplayCategory<Display> {
        @Override public CategoryIdentifier<Display> getCategoryIdentifier() { return TYPE; }
        @Override public Component getTitle() { return Component.translatable("recipe.hbm.black_book"); }
        @Override public Renderer getIcon() { return EntryStacks.of(MeteorBookContent.BOOK.get()); }
        @Override public int getDisplayHeight() { return 65; }
        @Override public int getDisplayWidth(Display display) { return 166; }
        @Override public List<Widget> setupDisplay(Display display, Rectangle bounds) {
            var widgets = new ArrayList<Widget>();
            widgets.add(Widgets.createDrawableWidget((graphics, mouseX, mouseY, partial) -> graphics.blit(BlackBookScreen.TEXTURE, bounds.x, bounds.y, 5, 11, 166, 65, 256, 256)));
            for (int i = 0; i < display.getInputEntries().size(); i++) widgets.add(Widgets.createSlot(new Point(bounds.x + 25 + i % 2 * 36, bounds.y + 6 + i / 2 * 36)).entries(display.getInputEntries().get(i)).disableBackground().markInput());
            widgets.add(Widgets.createSlot(new Point(bounds.x + 119, bounds.y + 24)).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput());
            return widgets;
        }
    }
}
