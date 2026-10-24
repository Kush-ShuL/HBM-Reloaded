package com.hbm.client.nuclear.waste;

import com.hbm.item.nuclear.waste.IsotopicWasteContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public final class IsotopicWasteReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry event) {
        for (var stack : IsotopicWasteContent.allStacks()) event.addEntry(EntryStacks.of(stack));
    }

}
