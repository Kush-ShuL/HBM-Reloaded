package com.hbm.client.meteorbook;

import com.hbm.item.meteorbook.MeteorBookContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class BlackBookClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event) { event.register(MeteorBookContent.MENU.get(), BlackBookScreen::new); }
    @SubscribeEvent public static void reload(RegisterClientReloadListenersEvent event) { event.registerReloadListener(BookGalacticLabels.INSTANCE); }
}
