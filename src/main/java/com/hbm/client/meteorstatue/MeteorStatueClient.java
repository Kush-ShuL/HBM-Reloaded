package com.hbm.client.meteorstatue;
import com.hbm.block.meteorstatue.MeteorStatueContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class MeteorStatueClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(MeteorStatueContent.ENTITY.get(),MeteorStatueRenderer::new);}
 private MeteorStatueClient(){}
}
