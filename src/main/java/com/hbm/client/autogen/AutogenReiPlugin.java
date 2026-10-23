package com.hbm.client.autogen;

import com.hbm.item.autogen.AutogenContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public final class AutogenReiPlugin implements REIClientPlugin {
    @Override public void registerItemComparators(ItemComparatorRegistry registration) { AutogenContent.all().forEach(holder->registration.registerComponents(holder.get())); }
    @Override public void registerEntries(EntryRegistry registration) { AutogenContent.creativeStacks().forEach(stack->registration.addEntry(EntryStacks.of(stack))); }
}
