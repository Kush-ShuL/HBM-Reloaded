package com.hbm.client.falling;

import com.hbm.registry.HbmFallingBlocks;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class LegacyFallingClient {
    private LegacyFallingClient() {}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(HbmFallingBlocks.ENTITY.get(), FallingBlockRenderer::new);
    }
}
