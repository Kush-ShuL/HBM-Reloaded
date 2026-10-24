package com.hbm.client.weapon.fireext;
import com.hbm.weapon.sedna.gun.fireext.FireExtContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class FireExtReiPlugin implements REIClientPlugin {
 @Override public void registerEntries(EntryRegistry event){for(var stack:FireExtContent.indexedItems())event.addEntry(EntryStacks.of(stack));}
}
