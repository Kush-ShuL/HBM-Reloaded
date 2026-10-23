package com.hbm.client.weapon.pepperbox;

import com.hbm.weapon.sedna.gun.PepperboxContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public final class PepperboxReiPlugin implements REIClientPlugin {
    @Override public void registerItemComparators(ItemComparatorRegistry event){event.register((context,stack)->0,PepperboxContent.PEPPERBOX.get());}
}
