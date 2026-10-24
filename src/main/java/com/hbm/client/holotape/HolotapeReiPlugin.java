package com.hbm.client.holotape;
import com.hbm.item.holotape.*;import me.shedaniel.rei.api.client.plugins.REIClientPlugin;import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;import me.shedaniel.rei.api.common.util.EntryStacks;import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient
public final class HolotapeReiPlugin implements REIClientPlugin {
 @Override public void registerEntries(EntryRegistry event){HolotapeContent.imageStacks().forEach(stack->event.addEntry(EntryStacks.of(stack)));}
}
