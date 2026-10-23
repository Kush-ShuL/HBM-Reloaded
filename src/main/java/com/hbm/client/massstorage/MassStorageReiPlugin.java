package com.hbm.client.massstorage;

import com.hbm.machine.massstorage.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public final class MassStorageReiPlugin implements REIClientPlugin {
    @Override public void registerItemComparators(ItemComparatorRegistry event){event.register((context,stack)->MassStorageItem.tier(stack),MassStorageContent.ITEM.get());}
    @Override public void registerEntries(EntryRegistry event){for(var stack:MassStorageContent.creativeStacks("machine"))event.addEntry(EntryStacks.of(stack));}
}
