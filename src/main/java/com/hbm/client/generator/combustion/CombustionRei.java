package com.hbm.client.generator.combustion;

import com.hbm.machine.generator.combustion.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public final class CombustionRei implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry registry){for(var stack:CombustionContent.pistonStacks())registry.addEntry(EntryStacks.of(stack));}
}
