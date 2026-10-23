package com.hbm.client.scaffold;

import com.hbm.block.scaffold.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public final class ScaffoldReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry registration) { ScaffoldContent.variants().forEach(stack -> registration.addEntry(EntryStacks.of(stack))); }
    @Override public void registerItemComparators(ItemComparatorRegistry registration) { registration.register((context, stack) -> SteelScaffoldItem.color(stack), ScaffoldContent.ITEM.get()); }
}
