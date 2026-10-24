package com.hbm.client.sourceapple;

import com.hbm.item.sourceapple.SourceApplesContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public final class SourceApplesReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry registration) {
        SourceApplesContent.indexedItems().forEach(stack -> registration.addEntry(EntryStacks.of(stack)));
    }
}
