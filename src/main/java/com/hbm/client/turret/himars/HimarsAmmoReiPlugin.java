package com.hbm.client.turret.himars;
import com.hbm.machine.turret.himars.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class HimarsAmmoReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry event){for(var stack:HimarsAmmoItem.creative())event.addEntry(EntryStacks.of(stack));}
}
