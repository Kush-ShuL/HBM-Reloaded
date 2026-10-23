package com.hbm.world.structure.templates.client;
import com.hbm.world.structure.templates.materials.TemplateMaterials;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class TemplateMaterialReiPlugin implements REIClientPlugin {
 @Override public void registerEntries(EntryRegistry event){TemplateMaterials.creativeStacks("block").forEach(stack->event.addEntry(EntryStacks.of(stack)));}
 @Override public void registerItemComparators(ItemComparatorRegistry event){for(var holder:TemplateMaterials.enums()){var item=holder.get();event.register((context,stack)->item.ordinal(stack),item);}}
}
