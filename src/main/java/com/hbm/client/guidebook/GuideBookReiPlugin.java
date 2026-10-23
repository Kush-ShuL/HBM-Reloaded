package com.hbm.client.guidebook;
import com.hbm.item.guidebook.*;import me.shedaniel.rei.api.client.plugins.REIClientPlugin;import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;import me.shedaniel.rei.api.common.util.EntryStacks;import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient
public final class GuideBookReiPlugin implements REIClientPlugin {
 @Override public void registerItemComparators(ItemComparatorRegistry event){event.register((context,stack)->GuideBookItem.metadata(stack),GuideBookContent.BOOK.get());}
 @Override public void registerEntries(EntryRegistry event){GuideBookContent.BOOK.get().visibleVariants().forEach(stack->event.addEntry(EntryStacks.of(stack)));}
}
