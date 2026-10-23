package com.hbm.client.containment;

import com.hbm.item.containment.ContainmentBoxContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ContainmentBoxClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(ContainmentBoxContent.MENU.get(),ContainmentBoxScreen::new);}
    private ContainmentBoxClient(){}
}
