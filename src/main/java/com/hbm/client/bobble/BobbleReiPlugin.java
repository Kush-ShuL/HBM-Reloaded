package com.hbm.client.bobble;
import com.hbm.block.bobble.BobbleContent;
import com.hbm.hazard.HbmHazards;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient
public final class BobbleReiPlugin implements REIClientPlugin {
 @Override public void registerEntries(EntryRegistry event){BobbleContent.creativeStacks("block").forEach(stack->event.addEntry(EntryStacks.of(stack)));}@Override public void registerItemComparators(ItemComparatorRegistry event){event.register((context,stack)->stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0),BobbleContent.ITEM.get());}
}
