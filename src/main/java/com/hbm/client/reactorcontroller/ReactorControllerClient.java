package com.hbm.client.reactorcontroller;

import com.hbm.machine.reactorcontroller.ReactorControllerContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ReactorControllerClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(ReactorControllerContent.MENU.get(),ReactorControllerScreen::new);}
    private ReactorControllerClient(){}
}
