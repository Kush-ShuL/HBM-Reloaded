package com.hbm.client.capacitors;
import com.hbm.machine.capacitors.CapacitorContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class CapacitorReiPlugin implements REIClientPlugin {@Override public void registerEntries(EntryRegistry event){CapacitorContent.allStacks().forEach(stack->event.addEntry(EntryStacks.of(stack)));}}
