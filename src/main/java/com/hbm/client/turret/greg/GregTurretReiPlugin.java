package com.hbm.client.turret.greg;
import com.hbm.machine.turret.greg.GregTurretContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class GregTurretReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry event){for(var stack:GregTurretContent.creativeStacks("weapon"))event.addEntry(EntryStacks.of(stack));}
}
