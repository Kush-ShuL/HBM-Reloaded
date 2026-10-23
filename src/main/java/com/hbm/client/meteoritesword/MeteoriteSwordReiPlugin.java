package com.hbm.client.meteoritesword;
import com.hbm.item.meteoritesword.MeteoriteSwordContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
/** Exact source hidden intermediates, no missing-item placeholder or fake recipe. */
@REIPluginClient
public final class MeteoriteSwordReiPlugin implements REIClientPlugin {@Override public void registerEntries(EntryRegistry registry){for(var stack:MeteoriteSwordContent.indexedItems())registry.removeEntry(EntryStacks.of(stack));}}
