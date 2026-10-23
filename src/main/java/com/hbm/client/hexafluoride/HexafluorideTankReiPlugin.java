package com.hbm.client.hexafluoride;
import com.hbm.machine.hexafluoride.HexafluorideTankContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class HexafluorideTankReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry event){HexafluorideTankContent.allStacks().forEach(stack->event.addEntry(EntryStacks.of(stack)));}
}
