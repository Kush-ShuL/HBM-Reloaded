package com.hbm.client.artillery;
import com.hbm.weapon.artillery.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class ArtilleryAmmoReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry event){for(var stack:ArtilleryAmmoItem.creative())event.addEntry(EntryStacks.of(stack));}
    @Override public void registerItemComparators(ItemComparatorRegistry event){event.register((context,stack)->ArtilleryAmmoItem.metadata(stack),ArtilleryAmmoContent.AMMO.get());}
}
