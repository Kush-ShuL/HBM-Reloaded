package com.hbm.client.ingotmetal;
import com.hbm.item.ingotmetal.FlatIngotMetalContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
/** Source NEI hidden technical items remain hidden from the native REI entry list. */
@REIPluginClient
public final class IngotMetalReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry registration){FlatIngotMetalContent.allStacks().stream().map(EntryStacks::of).forEach(registration::removeEntry);}
}
