package com.hbm.client.meteor;

import com.hbm.world.meteor.MeteorConfig;
import com.hbm.world.meteor.MeteorContent;
import com.hbm.world.meteor.MeteorEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class MeteorClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{MeteorEntity.CLIENT_TICK=meteor->MeteorClientEffects.tick(meteor,MeteorConfig.TAILS.get());MeteorEntity.CLIENT_REMOVE=MeteorClientEffects::removed;});}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(MeteorContent.METEOR.get(),MeteorRenderer::new);}
    private MeteorClient(){}
}
