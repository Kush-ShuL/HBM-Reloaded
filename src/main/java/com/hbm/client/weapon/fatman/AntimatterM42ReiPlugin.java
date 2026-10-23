package com.hbm.client.weapon.fatman;
import com.hbm.weapon.sedna.gun.fatman.AntimatterM42Content;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class AntimatterM42ReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry event){for(var stack:AntimatterM42Content.indexedItems())event.addEntry(EntryStacks.of(stack));}
    @Override public void registerItemComparators(ItemComparatorRegistry event){for(var stack:java.util.List.of(new net.minecraft.world.item.ItemStack(AntimatterM42Content.FATMAN.get())))event.register((context,value)->0,stack.getItem());}
}
