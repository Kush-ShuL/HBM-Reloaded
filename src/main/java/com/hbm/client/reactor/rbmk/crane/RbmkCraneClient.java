package com.hbm.client.reactor.rbmk.crane;
import com.hbm.machine.reactor.rbmk.crane.RbmkCraneContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RbmkCraneClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(RbmkCraneContent.ENTITY.get(),RbmkCraneRenderer::new);}
    @SubscribeEvent public static void keys(RegisterKeyMappingsEvent event){event.register(RbmkCraneKeys.UP);event.register(RbmkCraneKeys.DOWN);event.register(RbmkCraneKeys.LEFT);event.register(RbmkCraneKeys.RIGHT);event.register(RbmkCraneKeys.LOAD);}
    private RbmkCraneClient(){}
}
