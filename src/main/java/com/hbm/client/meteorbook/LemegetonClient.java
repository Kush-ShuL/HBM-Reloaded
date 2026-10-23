package com.hbm.client.meteorbook;

import com.hbm.item.lemegeton.LemegetonContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid="hbm", value=Dist.CLIENT, bus=EventBusSubscriber.Bus.MOD)
public final class LemegetonClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event) { event.register(LemegetonContent.MENU.get(), LemegetonScreen::new); }
    private LemegetonClient() { }
}
