package com.hbm.client.structure.fusionhatch;
import com.hbm.world.structure.dependencies.fusionhatch.FusionHatchContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;import me.shedaniel.rei.api.common.util.EntryStacks;import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class FusionHatchReiPlugin implements REIClientPlugin {
 @Override public void registerEntries(EntryRegistry event){for(var stack:FusionHatchContent.allStacks())event.addEntry(EntryStacks.of(stack));}
}
