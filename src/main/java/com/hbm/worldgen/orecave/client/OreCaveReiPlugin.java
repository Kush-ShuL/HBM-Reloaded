package com.hbm.worldgen.orecave.client;
import com.hbm.worldgen.orecave.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class OreCaveReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry event){for(var stack:OreCaves.indexedItems())event.addEntry(EntryStacks.of(stack));}
}
