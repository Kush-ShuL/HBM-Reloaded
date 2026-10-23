package com.hbm.client.conveyorgrabber;

import com.hbm.machine.conveyorgrabber.ConveyorGrabberContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** Model automatically uses the sole actual CraneBaseBlock model/event adapter, not a second renderer. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ConveyorGrabberClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(ConveyorGrabberContent.MENU.get(),ConveyorGrabberScreen::new);}
    private ConveyorGrabberClient(){}
}
