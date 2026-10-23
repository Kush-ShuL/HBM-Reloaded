package com.hbm.client.casingbag;

import com.hbm.item.casingbag.CasingBagContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class CasingBagClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event) { event.register(CasingBagContent.MENU.get(),CasingBagScreen::new); }
}
