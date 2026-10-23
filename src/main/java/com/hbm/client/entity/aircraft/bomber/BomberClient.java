package com.hbm.client.entity.aircraft.bomber;
import com.hbm.client.entity.aircraft.c130.C130Sound;
import com.hbm.entity.aircraft.bomber.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class BomberClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(BomberContent.BOMBER.get(),BomberRenderer::new);event.registerEntityRenderer(BomberContent.ZETA.get(),BombletZetaRenderer::new);event.registerEntityRenderer(BomberContent.PINK.get(),PinkCloudRenderer::new);}
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{EntityBomber.CLIENT_TICK=plane->C130Sound.keepAlive(plane,plane.style()<=4?"entity.bomberSmallLoop":"entity.bomberLoop");EntityBomber.CLIENT_REMOVE=C130Sound::remove;});}
 private BomberClient(){}
}
