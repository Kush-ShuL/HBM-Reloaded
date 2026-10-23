package com.hbm.client.model;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class LegacyModelEvents {
    private LegacyModelEvents() { }

    @SubscribeEvent
    public static void registerModelReload(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(LegacyObjModels.INSTANCE);
    }
}
