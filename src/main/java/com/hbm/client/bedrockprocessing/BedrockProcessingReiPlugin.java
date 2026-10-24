package com.hbm.client.bedrockprocessing;

import com.hbm.item.bedrockprocessing.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient public final class BedrockProcessingReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry registration){BedrockProcessingContent.indexedItems().forEach(stack->registration.addEntry(EntryStacks.of(stack)));}
}
