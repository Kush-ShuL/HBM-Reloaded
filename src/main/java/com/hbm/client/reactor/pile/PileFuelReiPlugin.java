package com.hbm.client.reactor.pile;

import com.hbm.machine.reactor.pile.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.forge.REIPluginClient;

/** The same nine source damage variants used by actual assembly/anvil/PUREX categories. */
@REIPluginClient
public final class PileFuelReiPlugin implements REIClientPlugin {
    @Override public void registerItemComparators(ItemComparatorRegistry event){event.register((context,stack)->PileFuelItem.type(stack).ordinal(),PileFuelContent.ITEM.get());}
}
