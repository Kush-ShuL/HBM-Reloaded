package com.hbm.client.turret.jeremy;
import com.hbm.machine.turret.jeremy.JeremyTurretContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class JeremyTurretReiPlugin implements REIClientPlugin {
    @Override public void registerItemComparators(ItemComparatorRegistry event){event.register((context,stack)->JeremyTurretContent.AMMO.get().ordinal(stack),JeremyTurretContent.AMMO.get());}
    @Override public void registerEntries(EntryRegistry event){for(var stack:JeremyTurretContent.creativeStacks("weapon"))event.addEntry(EntryStacks.of(stack));}
}
