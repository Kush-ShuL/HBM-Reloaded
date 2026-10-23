package com.hbm.client.creative;

import com.hbm.registry.HbmCreativeTabs;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public final class CreativeIconReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry registration) { HbmCreativeTabs.ORDER.stream().map(HbmCreativeTabs::uiIcon).map(EntryStacks::of).forEach(registration::removeEntry); }
}
