package com.hbm.client.junglemechanics;
import com.hbm.block.junglemechanics.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class JungleMechanicsReiPlugin implements REIClientPlugin {
 @Override public void registerEntries(EntryRegistry event){JungleMechanicsContent.allBlockStacks().forEach(stack->event.addEntry(EntryStacks.of(stack)));}
 @Override public void registerItemComparators(ItemComparatorRegistry event){for(var item:java.util.List.of(JungleMechanicsContent.TRAP_ITEM.get(),JungleMechanicsContent.GLYPH_ITEM.get()))event.register((context,stack)->JungleMetaItem.metadata(stack),item);}
}
