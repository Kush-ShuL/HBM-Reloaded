package com.hbm.client.rtgblast;

import com.hbm.client.alloy.AlloyReiPlugin;
import com.hbm.machine.rtgblast.*;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.client.registry.transfer.simple.SimpleTransferHandler;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient public final class RtgBlastReiPlugin implements REIClientPlugin {
    private static final CategoryIdentifier<Display> RTG=CategoryIdentifier.of("hbm","rtg");
    @Override public void registerCategories(CategoryRegistry registration){registration.addWorkstations(AlloyReiPlugin.TYPE,EntryStacks.of(RtgBlastContent.OFF_ITEM.get()));registration.addWorkstations(RTG,EntryStacks.of(RtgBlastContent.OFF_ITEM.get()));}
    @Override public void registerScreens(ScreenRegistry registration){registration.registerContainerClickArea(new Rectangle(101,35,24,17),RtgBlastScreen.class,AlloyReiPlugin.TYPE);}
    @Override public void registerTransferHandlers(TransferHandlerRegistry registration){registration.register(SimpleTransferHandler.create(RtgBlastMenu.class,AlloyReiPlugin.TYPE,new SimpleTransferHandler.IntRange(0,2)));}
}
