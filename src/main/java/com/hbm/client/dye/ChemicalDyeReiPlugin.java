package com.hbm.client.dye;
import com.hbm.item.dye.*;import me.shedaniel.rei.api.client.plugins.REIClientPlugin;import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class ChemicalDyeReiPlugin implements REIClientPlugin {
 @Override public void registerItemComparators(ItemComparatorRegistry event){event.register((context,stack)->ChemicalDyeItem.type(stack).ordinal(),ChemicalDyeContent.DYE.get(),ChemicalDyeContent.CRAYON.get());}
}
