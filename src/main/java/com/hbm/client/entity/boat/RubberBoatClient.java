package com.hbm.client.entity.boat;
import com.hbm.entity.boat.RubberBoatContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RubberBoatClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(RubberBoatContent.ENTITY.get(),RubberBoatRenderer::new);}
 private RubberBoatClient(){}
}
