package com.hbm.client.entity.radbeast;

import com.hbm.entity.mob.radbeast.RadiationBeastContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RadiationBeastClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(RadiationBeastContent.ELEMENTAL.get(),RadiationBeastRenderer::new);}
    private RadiationBeastClient(){}
}
