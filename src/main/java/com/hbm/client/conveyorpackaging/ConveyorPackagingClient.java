package com.hbm.client.conveyorpackaging;

import com.hbm.machine.conveyorpackaging.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ConveyorPackagingClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(ConveyorPackagingContent.BOXER_MENU.get(),ConveyorPackagingScreen::new);event.register(ConveyorPackagingContent.UNBOXER_MENU.get(),ConveyorPackagingScreen::new);}
    private ConveyorPackagingClient(){}
}
