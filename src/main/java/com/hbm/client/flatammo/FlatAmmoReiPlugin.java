package com.hbm.client.flatammo;

import com.hbm.weapon.sedna.ammo.FlatAmmoContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public final class FlatAmmoReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry registration){FlatAmmoContent.indexedItems().forEach(stack->registration.addEntry(EntryStacks.of(stack)));}
}
