package com.hbm.client.structure.woodfixtures46;

import com.hbm.block.woodfixtures46.WoodFixturesContent;
import com.hbm.block.woodfixtures46.WoodStructureItem;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient public final class WoodFixturesReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry event) { WoodFixturesContent.creativeStacks("block").forEach(stack -> event.addEntry(EntryStacks.of(stack))); }
    @Override public void registerItemComparators(ItemComparatorRegistry event) { event.register((context, stack) -> WoodStructureItem.type(stack), WoodFixturesContent.STRUCTURE_ITEM.get()); }
}
