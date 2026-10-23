package com.hbm.client.railway.cart;

import com.hbm.railway.cart.SourceCartBaseContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid="hbm",bus=EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class SourceCartBaseClient {
    @SubscribeEvent public static void layers(EntityRenderersEvent.RegisterLayerDefinitions event){event.registerLayerDefinition(SourceMinecartTestRenderer.LAYER,SourceMinecartTestRenderer::layer);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(SourceCartBaseContent.TEST.get(),SourceMinecartTestRenderer::new);}
    private SourceCartBaseClient(){}
}
