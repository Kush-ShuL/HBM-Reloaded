package com.hbm.client.bobmazon;
import com.hbm.bobmazon.BobmazonContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.world.item.ItemStack;
@REIPluginClient public final class BobmazonReiPlugin implements REIClientPlugin {
 @Override public void registerEntries(EntryRegistry registry){registry.removeEntry(EntryStacks.of(new ItemStack(BobmazonContent.HIDDEN.get())));}
}
