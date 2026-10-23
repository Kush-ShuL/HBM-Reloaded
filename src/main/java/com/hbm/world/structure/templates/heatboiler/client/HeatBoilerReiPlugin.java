package com.hbm.world.structure.templates.heatboiler.client;
import com.hbm.world.structure.templates.heatboiler.HeatBoilerContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient
public final class HeatBoilerReiPlugin implements REIClientPlugin {
    @Override public void registerItemComparators(ItemComparatorRegistry event){event.register((context,stack)->HeatBoilerContent.metadata(stack),HeatBoilerContent.ITEM.get());}
}
