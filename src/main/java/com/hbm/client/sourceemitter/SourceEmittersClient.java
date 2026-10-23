package com.hbm.client.sourceemitter;

import com.hbm.block.sourceemitter.SourceEmittersContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class SourceEmittersClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(SourceEmittersContent.LIGHT_ENTITY.get(), EmitterRenderer::new);
    }
    private SourceEmittersClient() { }
}
