package com.hbm.client.structure.woodfixtures46;

import com.hbm.block.woodfixtures46.WoodFixturesContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient public final class WoodFixturesReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry event) { WoodFixturesContent.creativeStacks("block").forEach(stack -> event.addEntry(EntryStacks.of(stack))); }
}
