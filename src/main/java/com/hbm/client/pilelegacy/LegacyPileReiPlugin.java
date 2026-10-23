package com.hbm.client.pilelegacy;

import com.hbm.machine.pilelegacy.LegacyPileContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public final class LegacyPileReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry registration){for(var stack:LegacyPileContent.allStacks())registration.addEntry(EntryStacks.of(stack));}
}
