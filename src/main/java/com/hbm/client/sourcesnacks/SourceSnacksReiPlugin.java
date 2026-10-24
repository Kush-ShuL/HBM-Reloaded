package com.hbm.client.sourcesnacks;

import com.hbm.item.sourcesnacks.SourceSnacksContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public final class SourceSnacksReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry event) {
        SourceSnacksContent.indexedItems().forEach(stack -> event.addEntry(EntryStacks.of(stack)));
    }
}
