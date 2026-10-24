package com.hbm.client.bobmazondeps;
import com.hbm.item.bobmazondeps.BobmazonDependenciesContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class BobmazonDependenciesReiPlugin implements REIClientPlugin {
 @Override public void registerEntries(EntryRegistry registry){for(var stack:BobmazonDependenciesContent.visibleTrinkets())registry.addEntry(EntryStacks.of(stack));for(var stack:BobmazonDependenciesContent.hiddenTrinkets())registry.removeEntry(EntryStacks.of(stack));}
}
