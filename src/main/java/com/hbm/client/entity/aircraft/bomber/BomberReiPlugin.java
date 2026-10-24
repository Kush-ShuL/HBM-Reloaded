package com.hbm.client.entity.aircraft.bomber;
import com.hbm.entity.aircraft.bomber.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class BomberReiPlugin implements REIClientPlugin {
 @Override public void registerEntries(EntryRegistry event){for(var stack:BomberContent.sourceVisible())event.addEntry(EntryStacks.of(stack));for(var stack:BomberContent.sourceHidden())event.removeEntry(EntryStacks.of(stack));}
}
