package com.hbm.client.siren;
import com.hbm.machine.siren.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class SirenReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry event){SirenContent.cassetteStacks().forEach(stack->event.addEntry(EntryStacks.of(stack)));event.removeEntry(EntryStacks.of(SirenContent.cassette(0)));}
}
