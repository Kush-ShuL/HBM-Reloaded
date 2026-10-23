package com.hbm.client.entity.aircraft.bomber;
import com.hbm.entity.aircraft.bomber.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class BomberReiPlugin implements REIClientPlugin {
 @Override public void registerEntries(EntryRegistry event){for(var stack:BomberContent.sourceVisible())event.addEntry(EntryStacks.of(stack));}
 @Override public void registerItemComparators(ItemComparatorRegistry event){event.register((context,stack)->ItemBombCaller.metadata(stack),BomberContent.CALLER.get(),BomberContent.RANGEFINDER.get());}
}
