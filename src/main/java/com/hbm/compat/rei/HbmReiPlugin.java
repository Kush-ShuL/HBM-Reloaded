package com.hbm.compat.rei;

import com.hbm.client.gui.ElectricFurnaceScreen;
import com.hbm.item.battery.BatteryContent;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import com.hbm.machine.electricfurnace.ElectricFurnaceContent;
import com.hbm.machine.electricfurnace.ElectricFurnaceMenu;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.client.registry.transfer.simple.SimpleTransferHandler;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

/** Uses the official REI vanilla-smelting category without importing its implementation module. */
@REIPluginClient
public final class HbmReiPlugin implements REIClientPlugin {
    private static final CategoryIdentifier<Display> SMELTING = CategoryIdentifier.of("minecraft", "plugins/smelting");
    @Override public void registerItemComparators(ItemComparatorRegistry registration) {
        registration.registerComponents(BatteryContent.CUBE_POWER.get(), BatteryContent.POTATO.get(), BatteryContent.MEMORY.get(), BatteryContent.PACK.get(), BatteryContent.SELF_CHARGING.get());
        com.hbm.registry.HbmEnumItems.all().forEach(holder -> registration.registerComponents(holder.get()));
        com.hbm.bomb.nuclear.NuclearContent.BREEDING_RODS.values().forEach(holder -> registration.register((context, stack) -> holder.get().ordinal(stack), holder.get()));
        com.hbm.registry.HbmOres.allItems().stream().map(holder -> holder.get())
                .filter(item -> item instanceof com.hbm.block.ore.LegacyOreItem ore && ore.spec().multi())
                .map(item -> (com.hbm.block.ore.LegacyOreItem) item)
                .forEach(item -> registration.register((context, stack) -> item.ordinal(stack), item));
        com.hbm.item.fluid.FluidItemContent.subtypedItems().forEach(item -> registration.register((context, stack) -> com.hbm.item.fluid.FluidItemContent.type(stack).getID(), item));
        com.hbm.block.pipe.PipeContent.subtypeItems().forEach(item -> registration.register((context, stack) -> stack.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(), 0), item));
        registration.register((context, stack) -> com.hbm.item.fluid.FluidItemContent.type(stack).getID(), com.hbm.block.pipe.PipeContent.TYPED_ITEM.get());
    }
    @Override public void registerCategories(CategoryRegistry registration) {
        registration.addWorkstations(SMELTING, EntryStacks.of(ElectricFurnaceContent.ITEM.get()));
    }
    @Override public void registerEntries(EntryRegistry registration) {
        com.hbm.item.fluid.FluidIconItem.recipeViewerStacks().forEach(stack -> registration.addEntry(EntryStacks.of(stack)));
        com.hbm.bomb.nuclear.NuclearContent.BREEDING_RODS.values().stream().flatMap(holder -> holder.get().creativeStacks().stream()).forEach(stack -> registration.addEntry(EntryStacks.of(stack)));
        java.util.stream.Stream.concat(
                com.hbm.registry.HbmEnumItems.tabNames().stream().flatMap(tab -> com.hbm.registry.HbmEnumItems.creativeStacks(tab).stream()),
                BatteryContent.enumItems().stream().flatMap(item -> item.creativeStacks().stream()))
                .forEach(stack -> registration.addEntry(EntryStacks.of(stack)));
        com.hbm.registry.HbmOres.allItems().stream().map(holder -> holder.get())
                .filter(item -> item instanceof com.hbm.block.ore.LegacyOreItem ore && ore.spec().multi())
                .flatMap(item -> ((com.hbm.block.ore.LegacyOreItem) item).creativeStacks().stream())
                .forEach(stack -> registration.addEntry(EntryStacks.of(stack)));
        java.util.List.of("parts", "control", "template").stream().flatMap(tab -> com.hbm.item.fluid.FluidItemContent.creativeStacks(tab).stream()).forEach(stack -> registration.addEntry(EntryStacks.of(stack)));
        java.util.List.of("machine", "template", "parts", "block").stream().flatMap(tab -> com.hbm.block.pipe.PipeContent.creativeStacks(tab).stream()).forEach(stack -> registration.addEntry(EntryStacks.of(stack)));
        registration.removeEntry(EntryStacks.of(ElectricFurnaceContent.ON_ITEM.get()));
        registration.removeEntry(EntryStacks.of(com.hbm.item.fluid.FluidItemContent.ICON.get()));
        registration.removeEntry(EntryStacks.of(BatteryContent.MEMORY.get().emptyStack()));
        registration.removeEntry(EntryStacks.of(BatteryContent.MEMORY.get().fullStack()));
    }
    @Override public void registerScreens(ScreenRegistry registration) {
        registration.registerContainerClickArea(new Rectangle(43, 36, 28, 12), ElectricFurnaceScreen.class, SMELTING);
    }
    @Override public void registerTransferHandlers(TransferHandlerRegistry registration) {
        registration.register(SimpleTransferHandler.create(ElectricFurnaceMenu.class, SMELTING, new SimpleTransferHandler.IntRange(1, 2)));
    }
}
