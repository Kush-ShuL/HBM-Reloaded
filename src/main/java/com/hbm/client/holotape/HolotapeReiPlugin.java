package com.hbm.client.holotape;
import com.hbm.item.holotape.*;import me.shedaniel.rei.api.client.plugins.REIClientPlugin;import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;import me.shedaniel.rei.api.common.util.EntryStacks;import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient
public final class HolotapeReiPlugin implements REIClientPlugin {
 @Override public void registerItemComparators(ItemComparatorRegistry event){event.register((context,stack)->HolotapeImageItem.metadata(stack),HolotapeContent.IMAGE.get());}
 @Override public void registerEntries(EntryRegistry event){HolotapeContent.IMAGE.get().variants().forEach(stack->event.addEntry(EntryStacks.of(stack)));}
}
