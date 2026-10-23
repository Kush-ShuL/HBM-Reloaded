package com.hbm.client.weapon.rifle762;
import com.hbm.weapon.sedna.gun.rifle762.Rifle762Content;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class Rifle762ReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry event){for(var stack:Rifle762Content.indexedItems())event.addEntry(EntryStacks.of(stack));}
    @Override public void registerItemComparators(ItemComparatorRegistry event){for(var stack:Rifle762Content.allStacks())event.register((context,value)->0,stack.getItem());}
}
