package com.hbm.client.meteorunstable;

import com.hbm.hazard.HbmHazards;
import com.hbm.item.meteorunstable.MeteorUnstableContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public final class MeteorUnstableReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry registration) {
        MeteorUnstableContent.metastableVariants().forEach(stack -> registration.addEntry(EntryStacks.of(stack)));
        MeteorUnstableContent.starmetalVariants().forEach(stack -> registration.addEntry(EntryStacks.of(stack)));
    }
    @Override public void registerItemComparators(ItemComparatorRegistry registration) {
        registration.register((context, stack) -> stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0), MeteorUnstableContent.METASTABLE.get());
    }
}
