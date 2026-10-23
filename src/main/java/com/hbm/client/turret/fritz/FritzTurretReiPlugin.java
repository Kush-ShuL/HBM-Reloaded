package com.hbm.client.turret.fritz;
import com.hbm.machine.turret.fritz.FritzTurretContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class FritzTurretReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry event){for(var stack:FritzTurretContent.creativeStacks("weapon"))event.addEntry(EntryStacks.of(stack));}
}
