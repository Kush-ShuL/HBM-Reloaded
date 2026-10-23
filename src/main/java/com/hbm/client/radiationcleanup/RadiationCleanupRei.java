package com.hbm.client.radiationcleanup;

import com.hbm.machine.radiationcleanup.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public final class RadiationCleanupRei implements REIClientPlugin {
    @Override public void registerItemComparators(ItemComparatorRegistry registration){registration.register((context,stack)->AbsorberBlock.rectify(RadiationCleanupContent.metadata(stack)),RadiationCleanupContent.ABSORBER_ITEM.get());}
    @Override public void registerEntries(EntryRegistry registration){for(var stack:RadiationCleanupContent.absorberStacks())registration.addEntry(EntryStacks.of(stack));}
}
