package com.hbm.world.structure.templates.microwave.client;
import com.hbm.world.structure.templates.microwave.MicrowaveContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class MicrowaveClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(MicrowaveContent.ENTITY.get(),MicrowaveRenderer::new);}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(MicrowaveContent.MENU.get(),MicrowaveScreen::new);}
    private MicrowaveClient(){}
}
