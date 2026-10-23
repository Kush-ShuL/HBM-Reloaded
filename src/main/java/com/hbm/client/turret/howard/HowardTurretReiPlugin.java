package com.hbm.client.turret.howard;
import com.hbm.machine.turret.howard.HowardTurretContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class HowardTurretReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry event){for(var stack:HowardTurretContent.creativeStacks("weapon"))event.addEntry(EntryStacks.of(stack));}
}
