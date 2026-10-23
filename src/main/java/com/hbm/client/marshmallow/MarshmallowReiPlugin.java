package com.hbm.client.marshmallow;

import com.hbm.item.marshmallow.ItemMarshmallow;
import com.hbm.item.marshmallow.MarshmallowContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public final class MarshmallowReiPlugin implements REIClientPlugin {
    @Override public void registerItemComparators(ItemComparatorRegistry event) { event.register((context, stack) -> ItemMarshmallow.metadata(stack), MarshmallowContent.MARSHMALLOW.get()); }
    @Override public void registerEntries(EntryRegistry event) { MarshmallowContent.indexedItems().forEach(stack -> event.addEntry(EntryStacks.of(stack))); }
}
