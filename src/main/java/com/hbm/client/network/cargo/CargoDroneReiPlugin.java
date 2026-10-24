package com.hbm.client.network.cargo;
import com.hbm.network.cargo.CargoDroneContent;
import com.hbm.network.cargo.DroneItem;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class CargoDroneReiPlugin implements REIClientPlugin {
 @Override public void registerEntries(EntryRegistry registration){for(var kind:DroneItem.Kind.values())registration.addEntry(EntryStacks.of(CargoDroneContent.droneStack(kind)));}
}
