package com.hbm.client.rebar;
import com.hbm.block.rebar.RebarContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class RebarReiPlugin implements REIClientPlugin {
 @Override public void registerEntries(EntryRegistry event){for(var tab:java.util.List.of("block","consumable"))for(var stack:RebarContent.creativeStacks(tab))event.addEntry(EntryStacks.of(stack));}
}
