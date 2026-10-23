package com.hbm.client.reactor.watz.fuel;

import com.hbm.machine.reactor.watz.fuel.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public final class WatzFuelRei implements REIClientPlugin {
    @Override public void registerItemComparators(ItemComparatorRegistry registry){for(var item:new net.minecraft.world.item.Item[]{WatzFuelContent.FRESH.get(),WatzFuelContent.DEPLETED.get()})registry.register((context,stack)->WatzPelletItem.metadata(stack),item);}
    @Override public void registerEntries(EntryRegistry registry){for(var stack:WatzFuelContent.stacks())registry.addEntry(EntryStacks.of(stack));}
}
