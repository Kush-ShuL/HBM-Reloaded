package com.hbm.client.weapon.b92;
import com.hbm.hazard.HbmHazards;import com.hbm.weapon.b92.B92Content;import me.shedaniel.rei.api.client.plugins.REIClientPlugin;import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient
public final class B92ReiPlugin implements REIClientPlugin {
 @Override public void registerItemComparators(ItemComparatorRegistry registration){registration.register((context,stack)->stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue()),B92Content.CELL.get());}
}
