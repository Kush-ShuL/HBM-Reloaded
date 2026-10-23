package com.hbm.client.weapontable;
import com.hbm.machine.weapontable.WeaponTableContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient
public final class WeaponTableReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry event){WeaponTableContent.indexedItems().forEach(stack->event.addEntry(EntryStacks.of(stack)));}
}
