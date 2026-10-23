package com.hbm.client.genericpart;
import com.hbm.item.genericpart.GenericPartContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient
public final class GenericPartReiPlugin implements REIClientPlugin {
 @Override public void registerEntries(EntryRegistry event){GenericPartContent.indexedItems().forEach(stack->event.addEntry(EntryStacks.of(stack)));}
}
