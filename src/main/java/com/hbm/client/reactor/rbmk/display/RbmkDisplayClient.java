package com.hbm.client.reactor.rbmk.display;
import com.hbm.machine.reactor.rbmk.display.RbmkDisplayContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RbmkDisplayClient{@SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(RbmkDisplayContent.ENTITY.get(),RbmkDisplayRenderer::new);}private RbmkDisplayClient(){}}
