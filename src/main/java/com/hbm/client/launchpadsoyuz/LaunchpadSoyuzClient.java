package com.hbm.client.launchpadsoyuz;

import com.hbm.machine.launchpadsoyuz.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class LaunchpadSoyuzClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(LaunchpadSoyuzContent.ENTITY.get(),LaunchpadSoyuzRenderer::new);LaunchpadSoyuzEntity.CLIENT_TICK=LaunchpadSoyuzEffects::tick;LaunchpadSoyuzEntity.CLIENT_REMOVE=LaunchpadSoyuzEffects::removed;}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(LaunchpadSoyuzContent.MENU.get(),LaunchpadSoyuzScreen::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private final BlockEntityWithoutLevelRenderer renderer=new LaunchpadSoyuzItemRenderer();@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer;}},LaunchpadSoyuzContent.ITEM.get());}
    private LaunchpadSoyuzClient(){}
}
