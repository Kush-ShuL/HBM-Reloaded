package com.hbm.client.soyuz;

import com.hbm.world.soyuz.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient public final class SoyuzCapsulesReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry event) { for (var stack : SoyuzCapsulesContent.soyuzSkins()) event.addEntry(EntryStacks.of(stack)); }
    @Override public void registerItemComparators(ItemComparatorRegistry event) { event.register((context, stack) -> SoyuzItem.metadata(stack), SoyuzCapsulesContent.SOYUZ.get()); }
}
