package com.hbm.client.autogen;

import com.hbm.item.autogen.AutogenContent;
import com.hbm.item.autogen.ScrapsItem;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.entry.comparison.EntryComparator;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.world.item.ItemStack;

@REIPluginClient
public final class AutogenReiPlugin implements REIClientPlugin {
    @Override public void registerItemComparators(ItemComparatorRegistry registration) {
        EntryComparator<ItemStack> exact=EntryComparator.itemComponents();
        AutogenContent.all().stream().filter(holder->holder.get() instanceof ScrapsItem).forEach(holder->
                registration.register((context,stack)->context.isExact()?exact.hash(context,stack):ScrapsItem.recipeIdentity(stack),holder.get()));
    }
    @Override public void registerEntries(EntryRegistry registration) { AutogenContent.viewerHiddenStacks().forEach(stack->registration.removeEntry(EntryStacks.of(stack))); AutogenContent.creativeStacks().forEach(stack->registration.addEntry(EntryStacks.of(stack))); }
}
