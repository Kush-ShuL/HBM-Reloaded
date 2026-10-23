package com.hbm.client.weapon.crucible;
import com.hbm.weapon.crucible.CrucibleContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class CrucibleReiPlugin implements REIClientPlugin {
 @Override public void registerEntries(EntryRegistry event){for(var stack:CrucibleContent.viewerStacks())event.addEntry(EntryStacks.of(stack));}
 @Override public void registerItemComparators(ItemComparatorRegistry event){event.register((context,stack)->stack.getDamageValue(),CrucibleContent.CRUCIBLE.get());}
}
