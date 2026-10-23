package com.hbm.client.ironfurnace;
import com.hbm.machine.ironfurnace.*;
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
@REIPluginClient public final class IronFurnaceReiPlugin implements REIClientPlugin {
    private static final CategoryIdentifier<Display> SMELTING=CategoryIdentifier.of("minecraft","plugins/smelting");
    @Override public void registerCategories(CategoryRegistry registry){registry.addWorkstations(SMELTING,EntryStacks.of(IronFurnaceContent.ITEM.get()));}
    @Override public void registerScreens(ScreenRegistry registry){registry.registerContainerClickArea(new Rectangle(70,16,18,18),IronFurnaceScreen.class,SMELTING);}
    @Override public void registerTransferHandlers(TransferHandlerRegistry registry){registry.register(SimpleTransferHandler.create(IronFurnaceMenu.class,SMELTING,new SimpleTransferHandler.IntRange(0,1)));}
}
