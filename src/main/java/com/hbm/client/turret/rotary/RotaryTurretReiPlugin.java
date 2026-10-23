package com.hbm.client.turret.rotary;
import com.hbm.machine.turret.rotary.RotaryTurretContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class RotaryTurretReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry event){for(var stack:RotaryTurretContent.creativeStacks("weapon"))event.addEntry(EntryStacks.of(stack));}
}
