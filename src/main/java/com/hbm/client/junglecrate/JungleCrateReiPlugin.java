package com.hbm.client.junglecrate;
import com.hbm.block.junglecrate.JungleCrateContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;import me.shedaniel.rei.api.common.util.EntryStacks;import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class JungleCrateReiPlugin implements REIClientPlugin {
 @Override public void registerEntries(EntryRegistry event){for(var stack:JungleCrateContent.allStacks())event.addEntry(EntryStacks.of(stack));}
}
