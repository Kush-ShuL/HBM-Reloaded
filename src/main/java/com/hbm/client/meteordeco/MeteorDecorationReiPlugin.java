package com.hbm.client.meteordeco;
import com.hbm.block.meteordeco.MeteorDecorationContent;
import com.hbm.hazard.HbmHazards;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient
public final class MeteorDecorationReiPlugin implements REIClientPlugin {
 @Override public void registerEntries(EntryRegistry event){MeteorDecorationContent.creativeStacks("machine").forEach(stack->event.addEntry(EntryStacks.of(stack)));}@Override public void registerItemComparators(ItemComparatorRegistry event){event.register((context,stack)->stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0),MeteorDecorationContent.FENCE_ITEM.get());}
}
