package com.hbm.client.entity.wastepearl;
import com.hbm.entity.grenade.waste.WastePearlContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class WastePearlClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(WastePearlContent.WASTE_PEARL.get(),WastePearlRenderer::new);}
    private WastePearlClient(){}
}
