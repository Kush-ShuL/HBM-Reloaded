package com.hbm.client.starcomponents;
import com.hbm.item.starcomponents.StarComponentsContent;import me.shedaniel.rei.api.client.plugins.REIClientPlugin;import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;import me.shedaniel.rei.api.common.util.EntryStacks;import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient
public final class StarComponentsReiPlugin implements REIClientPlugin {
 @Override public void registerItemComparators(ItemComparatorRegistry event){event.register((context,stack)->StarComponentsContent.ITEM.get().ordinal(stack),StarComponentsContent.ITEM.get());}
 @Override public void registerEntries(EntryRegistry event){StarComponentsContent.ITEM.get().creativeStacks().forEach(stack->event.addEntry(EntryStacks.of(stack)));}
}
