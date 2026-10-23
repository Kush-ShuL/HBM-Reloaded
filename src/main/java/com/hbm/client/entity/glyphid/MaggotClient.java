package com.hbm.client.entity.glyphid;

import com.hbm.entity.mob.glyphid.MaggotContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class MaggotClient {
    @SubscribeEvent public static void register(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(MaggotContent.MAGGOT.get(),MaggotRenderer::new);}
    private MaggotClient(){}
}
