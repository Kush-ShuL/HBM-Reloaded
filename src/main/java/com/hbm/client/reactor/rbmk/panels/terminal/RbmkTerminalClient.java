package com.hbm.client.reactor.rbmk.panels.terminal;
import com.hbm.client.reactor.rbmk.panels.RbmkRadioPanelClient;
import com.hbm.machine.reactor.rbmk.panels.terminal.RbmkTerminalContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RbmkTerminalClient{
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->RbmkRadioPanelClient.register(RbmkTerminalContent.ENTITY.get(),RbmkTerminalScreen::new));}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(RbmkTerminalContent.ENTITY.get(),RbmkTerminalRenderer::new);}private RbmkTerminalClient(){}
}
