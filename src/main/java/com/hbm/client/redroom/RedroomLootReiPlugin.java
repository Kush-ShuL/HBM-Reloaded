package com.hbm.client.redroom;
import com.hbm.item.redroom.RedroomLootContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;import me.shedaniel.rei.api.common.util.EntryStacks;import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class RedroomLootReiPlugin implements REIClientPlugin {
 @Override public void registerEntries(EntryRegistry event){for(var stack:RedroomLootContent.allStacks())event.addEntry(EntryStacks.of(stack));}
}
