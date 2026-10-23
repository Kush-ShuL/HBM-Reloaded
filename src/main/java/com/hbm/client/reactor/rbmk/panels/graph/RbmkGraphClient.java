package com.hbm.client.reactor.rbmk.panels.graph;
import com.hbm.client.reactor.rbmk.panels.RbmkRadioPanelClient;
import com.hbm.machine.reactor.rbmk.panels.graph.RbmkGraphContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RbmkGraphClient{
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->RbmkRadioPanelClient.register(RbmkGraphContent.ENTITY.get(),RbmkGraphScreen::new));}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(RbmkGraphContent.ENTITY.get(),RbmkGraphRenderer::new);}private RbmkGraphClient(){}
}
