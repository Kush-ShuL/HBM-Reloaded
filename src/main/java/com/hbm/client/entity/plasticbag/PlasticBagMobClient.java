package com.hbm.client.entity.plasticbag;
import com.hbm.entity.mob.plasticbag.PlasticBagMobContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class PlasticBagMobClient {
    private PlasticBagMobClient(){}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(PlasticBagMobContent.BAG_MOB.get(),PlasticBagMobRenderer::new);}
}
