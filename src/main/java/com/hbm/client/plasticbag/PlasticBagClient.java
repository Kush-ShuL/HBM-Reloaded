package com.hbm.client.plasticbag;

import com.hbm.item.plasticbag.PlasticBagContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid="hbm", value=Dist.CLIENT, bus=EventBusSubscriber.Bus.MOD)
public final class PlasticBagClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event) { event.register(PlasticBagContent.MENU.get(), PlasticBagScreen::new); }
}
