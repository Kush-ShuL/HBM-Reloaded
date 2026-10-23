package com.hbm.client.fensu;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

/**
 * Keep FEnSU's source additive, non-depth-writing geometry out of the shared
 * immediate BER buffer. Draw it against completed opaque OBJ depth, before
 * native translucent terrain, particles, clouds and weather composite.
 */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class FensuGlowPass {
    private FensuGlowPass() {}

    @EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
    public static final class Buffers {
        private Buffers() {}
        @SubscribeEvent
        public static void register(RegisterRenderBuffersEvent event) {
            event.registerRenderBuffer(FensuRenderTypes.ARC);
            event.registerRenderBuffer(FensuRenderTypes.plasma(FensuRenderer.PLASMA));
            event.registerRenderBuffer(FensuRenderTypes.plasma(FensuRenderer.SPARKLE));
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void render(RenderLevelStageEvent event) {
        if(event.getStage()!=RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) return;
        var buffers=Minecraft.getInstance().renderBuffers().bufferSource();
        // The final arbitrary-texture OBJ still occupies lastSharedType here:
        // native LevelRenderer has only ended named/fixed opaque types so far.
        buffers.endLastBatch();
        buffers.endBatch(FensuRenderTypes.ARC);
        buffers.endBatch(FensuRenderTypes.plasma(FensuRenderer.PLASMA));
        buffers.endBatch(FensuRenderTypes.plasma(FensuRenderer.SPARKLE));
    }
}
