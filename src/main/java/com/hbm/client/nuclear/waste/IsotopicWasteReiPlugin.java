package com.hbm.client.nuclear.waste;

import com.hbm.item.nuclear.waste.IsotopicWasteContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public final class IsotopicWasteReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry event) {
        for (var stack : IsotopicWasteContent.allStacks()) event.addEntry(EntryStacks.of(stack));
    }

    @Override public void registerItemComparators(ItemComparatorRegistry event) {
        for (var holder : IsotopicWasteContent.LONG_ITEMS) {
            var item = holder.get();
            event.register((context, stack) -> item.metadata(stack), item);
        }
        for (var holder : IsotopicWasteContent.SHORT_ITEMS) {
            var item = holder.get();
            event.register((context, stack) -> item.metadata(stack), item);
        }
    }
}
