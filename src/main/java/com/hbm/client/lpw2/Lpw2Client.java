package com.hbm.client.lpw2;

import com.hbm.machine.lpw2.Lpw2Content;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class Lpw2Client {
    @SubscribeEvent public static void renderer(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(Lpw2Content.ENTITY.get(),Lpw2Renderer::new);}
    private Lpw2Client(){}
}
