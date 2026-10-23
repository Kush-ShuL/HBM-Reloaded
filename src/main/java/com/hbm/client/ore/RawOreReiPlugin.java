package com.hbm.client.ore;

import com.hbm.ore.OreRecipeInputs;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@REIPluginClient
public final class RawOreReiPlugin implements REIClientPlugin {
    private static final CategoryIdentifier<Display> SMELTING = CategoryIdentifier.of("hbm", "raw_ore_smelting");
    private static final CategoryIdentifier<Display> BLASTING = CategoryIdentifier.of("hbm", "raw_ore_blasting");
    private static final ResourceLocation ARROW = ResourceLocation.withDefaultNamespace("container/furnace/burn_progress");
    @Override public void registerCategories(CategoryRegistry event) {
        event.add(new Category(false), new Category(true));
        event.addWorkstations(SMELTING, EntryStacks.of(Items.FURNACE), EntryStacks.of(com.hbm.machine.electricfurnace.ElectricFurnaceContent.ITEM.get()));
        event.addWorkstations(BLASTING, EntryStacks.of(Items.BLAST_FURNACE));
    }
    @Override public void registerDisplays(DisplayRegistry event) { RawOreSmeltingViews.recipes().forEach(recipe -> event.add(new Display(recipe))); }
    private static final class Display extends BasicDisplay {
        final RawOreSmeltingViews.View recipe;
        Display(RawOreSmeltingViews.View recipe) {
            super(List.of(EntryIngredient.of(recipe.inputs().stream().map(stack -> EntryStacks.of(stack)
                            .tooltipProcessor((entry, tooltip) -> tooltip.addAllTexts(OreRecipeInputs.tooltip(entry.getValue())))).toList())),
                    List.of(EntryIngredient.of(EntryStacks.of(recipe.output()).tooltipProcessor((entry, tooltip) ->
                            tooltip.addAllTexts(List.of(Component.translatable("gui.hbm.raw_ore.source_output")))))), Optional.of(recipe.id()));
            this.recipe = recipe;
        }
        @Override public CategoryIdentifier<Display> getCategoryIdentifier() { return recipe.blasting() ? BLASTING : SMELTING; }
    }
    private static final class Category implements DisplayCategory<Display> {
        final boolean blasting;
        Category(boolean blasting) { this.blasting = blasting; }
        @Override public CategoryIdentifier<Display> getCategoryIdentifier() { return blasting ? BLASTING : SMELTING; }
        @Override public Component getTitle() { return Component.translatable(blasting ? "gui.hbm.raw_ore.blasting" : "gui.hbm.raw_ore.smelting"); }
        @Override public Renderer getIcon() { return EntryStacks.of(blasting ? Items.BLAST_FURNACE : Items.FURNACE); }
        @Override public int getDisplayWidth(Display display) { return 110; }
        @Override public int getDisplayHeight() { return 40; }
        @Override public List<Widget> setupDisplay(Display display, Rectangle bounds) {
            List<Widget> widgets = new ArrayList<>();
            widgets.add(Widgets.createDrawableWidget((graphics, x, y, delta) -> graphics.blitSprite(ARROW, bounds.x + 43, bounds.y + 11, 24, 16)));
            widgets.add(Widgets.createSlot(new Point(bounds.x + 5, bounds.y + 11)).entries(display.getInputEntries().getFirst()).markInput());
            widgets.add(Widgets.createSlot(new Point(bounds.x + 85, bounds.y + 11)).entries(display.getOutputEntries().getFirst()).markOutput());
            return widgets;
        }
    }
}
