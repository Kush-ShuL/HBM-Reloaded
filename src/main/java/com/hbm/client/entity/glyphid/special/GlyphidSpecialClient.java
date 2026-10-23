package com.hbm.client.entity.glyphid.special;

import com.hbm.client.entity.glyphid.GlyphidRenderer;
import com.hbm.entity.mob.glyphid.special.GlyphidSpecialContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class GlyphidSpecialClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(GlyphidSpecialContent.DIGGER.get(), GlyphidRenderer::new);
        event.registerEntityRenderer(GlyphidSpecialContent.NUCLEAR.get(), GlyphidNuclearRenderer::new);
    }
    private GlyphidSpecialClient() { }
}
