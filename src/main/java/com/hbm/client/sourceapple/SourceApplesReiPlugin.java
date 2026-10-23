package com.hbm.client.sourceapple;

import com.hbm.item.sourceapple.SourceApplesContent;
import com.hbm.item.sourceapple.SourceMetalAppleItem;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.world.item.Item;

@REIPluginClient
public final class SourceApplesReiPlugin implements REIClientPlugin {
    @Override public void registerItemComparators(ItemComparatorRegistry registration) {
        registration.register((context, stack) -> SourceMetalAppleItem.metadata(stack),
                SourceApplesContent.subtypedItems().toArray(Item[]::new));
    }
    @Override public void registerEntries(EntryRegistry registration) {
        SourceApplesContent.indexedItems().forEach(stack -> registration.addEntry(EntryStacks.of(stack)));
    }
}
