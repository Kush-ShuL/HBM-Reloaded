package com.hbm.client.oilplant;
import com.hbm.block.oilplant.OilBotanyContent;import me.shedaniel.rei.api.client.plugins.REIClientPlugin;import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;import me.shedaniel.rei.api.common.util.EntryStacks;import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class OilBotanyReiPlugin implements REIClientPlugin {@Override public void registerEntries(EntryRegistry e){for(var tab:java.util.List.of("block","parts"))for(var stack:OilBotanyContent.creativeStacks(tab))e.addEntry(EntryStacks.of(stack));}}
