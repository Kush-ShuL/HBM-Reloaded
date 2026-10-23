package com.hbm.client.electricpress;
import com.hbm.client.press.PressReiPlugin;
import com.hbm.machine.electricpress.*;
import java.util.*;
import java.util.stream.IntStream;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.client.registry.transfer.simple.SimpleTransferHandler;
import me.shedaniel.rei.api.common.transfer.info.stack.SlotAccessor;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
@REIPluginClient public final class ElectricPressReiPlugin implements REIClientPlugin {
    @Override public void registerCategories(CategoryRegistry event){event.addWorkstations(PressReiPlugin.TYPE,EntryStacks.of(ElectricPressContent.ELECTRIC_ITEM.get()),EntryStacks.of(ElectricPressContent.CONVEYOR_ITEM.get()));}
    @Override public void registerScreens(ScreenRegistry event){event.registerContainerClickArea(new Rectangle(37,23,24,18),ElectricPressScreen.class,PressReiPlugin.TYPE);}
    @Override public void registerTransferHandlers(TransferHandlerRegistry event){event.register(new SimpleTransferHandler(){@Override public Iterable<SlotAccessor> getInputSlots(Context context){return List.of(SlotAccessor.fromSlot(context.getMenu().getSlot(2)),SlotAccessor.fromSlot(context.getMenu().getSlot(1)));}@Override public Iterable<SlotAccessor> getInventorySlots(Context context){return IntStream.range(5,41).mapToObj(index->SlotAccessor.fromSlot(context.getMenu().getSlot(index))).toList();}@Override public Result handle(Context context){if(!(context.getMenu() instanceof ElectricPressMenu)||!context.getDisplay().getCategoryIdentifier().equals(PressReiPlugin.TYPE))return Result.createNotApplicable();return SimpleTransferHandler.super.handle(context);}});}
}
