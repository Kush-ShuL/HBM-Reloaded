package com.hbm.satellite.viewer;
import com.hbm.satellite.SatelliteContent;import com.hbm.satellite.item.SatelliteItem;import me.shedaniel.rei.api.client.plugins.REIClientPlugin;import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;import me.shedaniel.rei.api.common.util.EntryStacks;import me.shedaniel.rei.forge.REIPluginClient;
/** Uses existing native REI registry/comparator, without another item list or fabricated category. */
@REIPluginClient public final class SatelliteReiPlugin implements REIClientPlugin {
 @Override public void registerEntries(EntryRegistry event){SatelliteItem.creativeStacks().forEach(stack->event.addEntry(EntryStacks.of(stack)));}
}
