package com.hbm.client.structure.toaster;

import com.hbm.block.toaster.ToasterContent;
import com.hbm.block.toaster.ToasterItem;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient public final class ToasterReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry event) { ToasterContent.creativeStacks("block").forEach(stack -> event.addEntry(EntryStacks.of(stack))); }
    @Override public void registerItemComparators(ItemComparatorRegistry event) { event.register((context, stack) -> ToasterItem.variant(stack), ToasterContent.ITEM.get()); }
}
