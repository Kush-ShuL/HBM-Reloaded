package com.hbm.client.entity.aircraft.c130;

import com.hbm.entity.aircraft.c130.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class C130Client {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(C130Content.C130.get(), C130Renderer::new);
        event.registerEntityRenderer(C130Content.PARACHUTE_CRATE.get(), ParachuteCrateRenderer::new);
    }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> { EntityC130.CLIENT_TICK = C130Sound::keepAlive; EntityC130.CLIENT_REMOVE = C130Sound::remove; });
    }
    private C130Client() {}
}
