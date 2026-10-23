package com.hbm.client.weapon.pistols;
import com.hbm.weapon.sedna.gun.pistols.PistolContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class PistolReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry event){for(var stack:PistolContent.indexedItems())event.addEntry(EntryStacks.of(stack));}
    @Override public void registerItemComparators(ItemComparatorRegistry event){for(var stack:PistolContent.allStacks())event.register((context,value)->0,stack.getItem());}
}
