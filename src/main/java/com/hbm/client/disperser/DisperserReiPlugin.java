package com.hbm.client.disperser;
import com.hbm.item.disperser.DisperserContent;
import com.hbm.item.fluid.FluidItemContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class DisperserReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry registration){DisperserContent.variants().forEach(stack->registration.addEntry(EntryStacks.of(stack)));}
    @Override public void registerItemComparators(ItemComparatorRegistry registration){for(var item:java.util.List.of(DisperserContent.CANISTER.get(),DisperserContent.GLAND.get()))registration.register((context,stack)->FluidItemContent.type(stack).getID(),item);}
}
