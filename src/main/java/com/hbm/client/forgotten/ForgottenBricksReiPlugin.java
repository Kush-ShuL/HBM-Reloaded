package com.hbm.client.forgotten;

import com.hbm.block.forgotten.ForgottenBricksContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

/** All legitimate old metadata-nibble entries are hidden, exactly these two source block items. */
@REIPluginClient
public final class ForgottenBricksReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry registry){for(var stack:ForgottenBricksContent.hiddenStacks())registry.removeEntry(EntryStacks.of(stack));}
}
