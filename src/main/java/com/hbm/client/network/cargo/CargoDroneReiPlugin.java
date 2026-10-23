package com.hbm.client.network.cargo;
import com.hbm.network.cargo.CargoDroneContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class CargoDroneReiPlugin implements REIClientPlugin {
 @Override public void registerEntries(EntryRegistry registration){int size=net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.containsKey(net.minecraft.resources.ResourceLocation.parse("hbm:entity_request_drone"))?5:4;for(int i=0;i<size;i++)registration.addEntry(EntryStacks.of(CargoDroneContent.DRONE.get().stack(i)));}@Override public void registerItemComparators(ItemComparatorRegistry registration){registration.register((context,stack)->CargoDroneContent.DRONE.get().ordinal(stack),CargoDroneContent.DRONE.get());}
}
