package com.hbm.client.structure.vending;
import com.hbm.world.structure.vending.*;import me.shedaniel.rei.api.client.plugins.REIClientPlugin;import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;import me.shedaniel.rei.api.common.util.EntryStacks;import me.shedaniel.rei.forge.REIPluginClient;
/** Uses existing native REI registry, without another item list or fabricated category. */
@REIPluginClient public final class VendingMachineReiPlugin implements REIClientPlugin {
 @Override public void registerEntries(EntryRegistry event){VendingMachineContent.creativeStacks("block").forEach(stack->event.addEntry(EntryStacks.of(stack)));}
}
