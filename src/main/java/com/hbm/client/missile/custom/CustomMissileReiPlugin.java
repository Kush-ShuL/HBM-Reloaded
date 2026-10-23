package com.hbm.client.missile.custom;

import com.hbm.missile.custom.CustomMissileContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;

@REIPluginClient
public final class CustomMissileReiPlugin implements REIClientPlugin {
    @Override public void registerEntries(EntryRegistry event) { CustomMissileContent.creativeStacks("missile").forEach(stack -> event.addEntry(EntryStacks.of(stack))); }
    @Override public void registerItemComparators(ItemComparatorRegistry event) {
        event.register((context, stack) -> stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().hashCode(), CustomMissileContent.MISSILE.get());
    }
}
