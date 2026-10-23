package com.hbm.client.autocrafter;
import com.hbm.machine.autocrafter.AutocrafterContent;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
@REIPluginClient public final class AutocrafterReiPlugin implements REIClientPlugin {
    private static final CategoryIdentifier<?> CRAFTING=CategoryIdentifier.of("minecraft","plugins/crafting");
    @Override public void registerCategories(CategoryRegistry event){event.addWorkstations(CRAFTING,EntryStacks.of(AutocrafterContent.ITEM.get()));}
    @Override public void registerScreens(ScreenRegistry event){event.registerContainerClickArea(new Rectangle(98,40,16,16),AutocrafterScreen.class,CRAFTING);}
}
