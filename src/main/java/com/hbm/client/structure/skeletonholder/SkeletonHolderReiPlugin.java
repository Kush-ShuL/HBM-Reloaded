package com.hbm.client.structure.skeletonholder;
import com.hbm.block.skeletonholder.SkeletonHolderContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class SkeletonHolderReiPlugin implements REIClientPlugin {
 @Override public void registerEntries(EntryRegistry registry){registry.addEntry(EntryStacks.of(SkeletonHolderContent.ITEM.get()));}
}
