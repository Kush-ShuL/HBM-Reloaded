package com.hbm.client.rtgfurnace;

import com.hbm.machine.rtgfurnace.*;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.client.registry.transfer.simple.SimpleTransferHandler;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient public final class RtgFurnaceReiPlugin implements REIClientPlugin {
    private static final CategoryIdentifier<Display> SMELTING=CategoryIdentifier.of("minecraft","plugins/smelting");
    @Override public void registerEntries(EntryRegistry registry){registry.removeEntry(EntryStacks.of(RtgFurnaceContent.ON_ITEM.get()));}
    @Override public void registerCategories(CategoryRegistry registry){registry.addWorkstations(SMELTING,EntryStacks.of(RtgFurnaceContent.OFF_ITEM.get()));}
    @Override public void registerScreens(ScreenRegistry registry){registry.registerContainerClickArea(new Rectangle(79,34,24,17),RtgFurnaceScreen.class,SMELTING);}
    @Override public void registerTransferHandlers(TransferHandlerRegistry registry){registry.register(SimpleTransferHandler.create(RtgFurnaceMenu.class,SMELTING,new SimpleTransferHandler.IntRange(0,1)));}
}
