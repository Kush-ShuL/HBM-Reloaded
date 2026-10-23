package com.hbm.client.reactor.dfc;
import com.hbm.machine.reactor.dfc.DfcCoreContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class DfcCoreClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(DfcCoreContent.ENTITY.get(),DfcCoreRenderer::new);}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(DfcCoreContent.MENU.get(),DfcCoreScreen::new);}
    private DfcCoreClient(){}
}
