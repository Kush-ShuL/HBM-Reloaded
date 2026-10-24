package com.hbm.client.bedrockprocessing;

import com.hbm.item.bedrockprocessing.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient public final class BedrockProcessingReiPlugin implements REIClientPlugin {
    @Override public void registerItemComparators(ItemComparatorRegistry registration){registration.register((context,stack)->stack.getItem() instanceof LegacyBedrockItem item?item.ordinal(stack):BedrockProcessingData.canonical(BedrockProcessingData.metadata(stack)),BedrockProcessingContent.subtypedItems().toArray(net.minecraft.world.item.Item[]::new));}
    @Override public void registerEntries(EntryRegistry registration){BedrockProcessingContent.indexedItems().forEach(stack->registration.addEntry(EntryStacks.of(stack)));}
}
