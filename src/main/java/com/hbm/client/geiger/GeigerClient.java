package com.hbm.client.geiger;

import com.hbm.geiger.GeigerContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class GeigerClient {
    private GeigerClient() {}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerBlockEntityRenderer(GeigerContent.ENTITY.get(),GeigerRenderer::new); }
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) { event.registerItem(new IClientItemExtensions() {
        private GeigerItemRenderer renderer;
        @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() { if(renderer==null) renderer=new GeigerItemRenderer();return renderer; }
    },GeigerContent.BLOCK_ITEM.get()); }
}
