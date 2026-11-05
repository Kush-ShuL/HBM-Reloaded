package com.hbm.client.oilplant;
import com.hbm.block.oilplant.OilBotanyContent;import me.shedaniel.rei.api.client.plugins.REIClientPlugin;import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;import me.shedaniel.rei.api.common.util.EntryStacks;import me.shedaniel.rei.forge.REIPluginClient;
import com.hbm.hazard.HbmHazards;
import me.shedaniel.rei.api.common.entry.comparison.EntryComparator;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import net.minecraft.world.item.ItemStack;
@REIPluginClient public final class OilBotanyReiPlugin implements REIClientPlugin {
 @Override public void registerEntries(EntryRegistry e){for(var tab:java.util.List.of("block","parts"))for(var stack:OilBotanyContent.creativeStacks(tab))e.addEntry(EntryStacks.of(stack));}
 @Override public void registerItemComparators(ItemComparatorRegistry event){
  EntryComparator<ItemStack> exact=EntryComparator.itemComponents();
  for(var item:java.util.List.of(OilBotanyContent.FLOWER_ITEM.get(),OilBotanyContent.TALL_ITEM.get()))
   event.register((context,stack)->context.isExact()?exact.hash(context,stack):stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0),item);
 }
}
