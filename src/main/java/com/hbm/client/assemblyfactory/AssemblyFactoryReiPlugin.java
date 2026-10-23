package com.hbm.client.assemblyfactory;
import com.hbm.machine.assemblyfactory.AssemblyFactoryContent;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
/** Same native assembly recipes/config/blueprint display, without another cloned recipe table/category. */
@REIPluginClient
public final class AssemblyFactoryReiPlugin implements REIClientPlugin {
    @Override public void registerCategories(CategoryRegistry event){event.addWorkstations(CategoryIdentifier.of("hbm","assembly"),EntryStacks.of(AssemblyFactoryContent.ITEM.get()));}
}
