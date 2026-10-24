package com.hbm.client.guidebook;

import com.hbm.item.guidebook.GuideBookContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public final class GuideBookReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry event) {
        GuideBookContent.hiddenStacks().forEach(stack -> event.removeEntry(EntryStacks.of(stack)));
        GuideBookContent.visibleStacks().forEach(stack -> event.addEntry(EntryStacks.of(stack)));
    }
}
