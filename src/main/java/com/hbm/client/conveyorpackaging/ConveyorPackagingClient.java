package com.hbm.client.conveyorpackaging;

import com.hbm.machine.conveyorpackaging.*;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ConveyorPackagingClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(ConveyorPackagingContent.BOXER_MENU.get(),ConveyorPackagingScreen::new);event.register(ConveyorPackagingContent.UNBOXER_MENU.get(),ConveyorPackagingScreen::new);}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{for(var item:java.util.List.of(ConveyorPackagingContent.STACK.get(),ConveyorPackagingContent.EJECTOR.get()))ItemProperties.register(item,ResourceLocation.parse("hbm:legacy_metadata"),(stack,level,entity,seed)->CraneMetaUpgradeItem.icon(stack));});}
    private ConveyorPackagingClient(){}
}
