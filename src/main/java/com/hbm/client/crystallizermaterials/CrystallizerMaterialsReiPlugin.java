package com.hbm.client.crystallizermaterials;
import com.hbm.material.crystallizer.CrystallizerMaterials;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;import me.shedaniel.rei.api.common.util.EntryStacks;import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class CrystallizerMaterialsReiPlugin implements REIClientPlugin {
 @Override public void registerEntries(EntryRegistry event){for(var stack:CrystallizerMaterials.allStacks())event.addEntry(EntryStacks.of(stack));}
 @Override public void registerItemComparators(me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry event){event.register((context,stack)->CrystallizerMaterials.PLASTIC.get().ordinal(stack),CrystallizerMaterials.PLASTIC.get());}
}
