package com.hbm.client.crt;
import com.hbm.block.crt.DecoCRTContent;
import com.hbm.block.crt.DecoCRTItem;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class DecoCRTReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry event){DecoCRTContent.variants().forEach(stack->event.addEntry(EntryStacks.of(stack)));}
    @Override public void registerItemComparators(ItemComparatorRegistry event){event.register((context,stack)->DecoCRTItem.variant(stack),DecoCRTContent.ITEM.get());}
}
