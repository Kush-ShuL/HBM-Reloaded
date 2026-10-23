package com.hbm.client.reactor.rbmk.panels.gauge;
import com.hbm.client.reactor.rbmk.panels.RbmkRadioPanelClient;
import com.hbm.machine.reactor.rbmk.panels.gauge.RbmkGaugeContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RbmkGaugeClient{
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->RbmkRadioPanelClient.register(RbmkGaugeContent.ENTITY.get(),RbmkGaugeScreen::new));}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(RbmkGaugeContent.ENTITY.get(),RbmkGaugeRenderer::new);}private RbmkGaugeClient(){}
}
