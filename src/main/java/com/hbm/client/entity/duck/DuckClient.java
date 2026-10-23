package com.hbm.client.entity.duck;

import com.hbm.entity.mob.duck.DuckContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class DuckClient {
    @SubscribeEvent public static void register(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(DuckContent.DUCK.get(),context->new DuckRenderer(context,false));
        event.registerEntityRenderer(DuckContent.QUACKOS.get(),context->new DuckRenderer(context,true));
    }
    private DuckClient(){}
}
