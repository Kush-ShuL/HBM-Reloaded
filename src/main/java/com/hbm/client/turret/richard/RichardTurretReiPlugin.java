package com.hbm.client.turret.richard;
import com.hbm.machine.turret.richard.RichardTurretContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class RichardTurretReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry event){for(var stack:RichardTurretContent.creativeStacks("weapon"))event.addEntry(EntryStacks.of(stack));}
}
