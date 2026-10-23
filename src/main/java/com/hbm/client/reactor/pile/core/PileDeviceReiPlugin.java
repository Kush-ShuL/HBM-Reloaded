package com.hbm.client.reactor.pile.core;
import com.hbm.machine.reactor.pile.core.*;import me.shedaniel.rei.api.client.plugins.REIClientPlugin;import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class PileDeviceReiPlugin implements REIClientPlugin {@Override public void registerItemComparators(ItemComparatorRegistry event){event.register((context,stack)->Math.abs(PileDeviceItem.metadata(stack)%3),PileCoreContent.DEVICE_ITEM.get());}}
