package com.hbm.client.machine.pa.parts;
import com.hbm.machine.pa.parts.PaPartsContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class PaPartsReiPlugin implements REIClientPlugin{
    @Override public void registerEntries(EntryRegistry event){for(var stack:PaPartsContent.coilStacks())event.addEntry(EntryStacks.of(stack));}
}
