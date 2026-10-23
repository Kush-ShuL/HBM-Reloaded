package com.hbm.client.structure.materials45;
import com.hbm.world.structure.dependencies.materials45.StructuralMaterials45Content;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;import me.shedaniel.rei.api.common.util.EntryStacks;import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class StructuralMaterials45ReiPlugin implements REIClientPlugin {
 @Override public void registerEntries(EntryRegistry event){for(var stack:StructuralMaterials45Content.plateStacks())event.addEntry(EntryStacks.of(stack));}
 @Override public void registerItemComparators(me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry event){event.register((context,stack)->com.hbm.world.structure.dependencies.materials45.PlatemetalBlock.rectify(stack.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),0)),StructuralMaterials45Content.PLATEMETAL_ITEM.get());}
}
