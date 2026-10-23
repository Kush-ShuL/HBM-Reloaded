package com.hbm.client.soyuzlauncher;

import com.hbm.entity.rocket.EntityRocketSoyuz;
import com.hbm.machine.soyuzlauncher.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class SoyuzLauncherClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerBlockEntityRenderer(SoyuzLauncherContent.ENTITY.get(),SoyuzLauncherRenderer::new);
        event.registerBlockEntityRenderer(SoyuzLauncherContent.STRUCTURE_ENTITY.get(),SoyuzStructureRenderer::new);
        event.registerEntityRenderer(SoyuzLauncherContent.ROCKET.get(),SoyuzRocketRenderer::new);
        SoyuzLauncherBlockEntity.CLIENT_TICK=SoyuzLauncherEffects::tick;SoyuzLauncherBlockEntity.CLIENT_REMOVE=SoyuzLauncherEffects::removed;EntityRocketSoyuz.CLIENT_TICK=SoyuzLauncherEffects::rocketTick;
    }
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(SoyuzLauncherContent.MENU.get(),SoyuzLauncherScreen::new);}
    private SoyuzLauncherClient(){}
}
