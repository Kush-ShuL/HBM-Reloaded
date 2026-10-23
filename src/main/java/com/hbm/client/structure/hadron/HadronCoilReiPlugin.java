package com.hbm.client.structure.hadron;
import com.hbm.world.structure.dependencies.hadron.HadronCoilContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;import me.shedaniel.rei.api.common.util.EntryStacks;import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class HadronCoilReiPlugin implements REIClientPlugin {
 @Override public void registerEntries(EntryRegistry event){for(var stack:HadronCoilContent.allStacks())event.addEntry(EntryStacks.of(stack));}
}
