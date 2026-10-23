package com.hbm.client.structure.materials;
import com.hbm.world.structure.dependencies.materials.StructureMaterialsContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;import me.shedaniel.rei.api.common.util.EntryStacks;import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class StructureMaterialsReiPlugin implements REIClientPlugin {
 @Override public void registerEntries(EntryRegistry event){for(var stack:StructureMaterialsContent.allStacks())event.addEntry(EntryStacks.of(stack));}
}
