package com.hbm.client.sourceslab;

import com.hbm.block.masonry.LegacyMultiSlabBlock;
import com.hbm.block.sourceslab.SourceSlabsContent;
import com.hbm.hazard.HbmHazards;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public final class SourceSlabsReiPlugin implements REIClientPlugin {
    @Override public void registerItemComparators(ItemComparatorRegistry registration) {
        SourceSlabsContent.stoneItems().forEach(item -> registration.register((context, stack) ->
            ((LegacyMultiSlabBlock)item.getBlock()).material(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0)), item));
    }
    @Override public void registerEntries(EntryRegistry registration) {
        SourceSlabsContent.creativeStacks("block").forEach(stack -> registration.addEntry(EntryStacks.of(stack)));
        registration.removeEntry(EntryStacks.of(SourceSlabsContent.PINK_ITEM.get()));
        registration.removeEntry(EntryStacks.of(SourceSlabsContent.PINK_DOUBLE_ITEM.get()));
        registration.removeEntry(EntryStacks.of(SourceSlabsContent.STONE_DOUBLE_ITEM.get()));
    }
}
