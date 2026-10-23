package com.hbm.client.sourcestones;

import com.hbm.block.sourcestones.SourceStonesContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient public final class SourceStonesReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry event){for(var stack:SourceStonesContent.biomeStacks())event.addEntry(EntryStacks.of(stack));}
    @Override public void registerItemComparators(ItemComparatorRegistry event){event.register((context,stack)->SourceStonesContent.BIOME_ITEM.get().ordinal(stack),SourceStonesContent.BIOME_ITEM.get());}
}
