package com.hbm.client.radgen;

import com.hbm.machine.radgen.RadGenContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class RadGenClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerBlockEntityRenderer(RadGenContent.ENTITY.get(), RadGenRenderer::new); }
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event) { event.register(RadGenContent.MENU.get(), RadGenScreen::new); }
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() { private RadGenItemRenderer renderer; @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() { return renderer == null ? (renderer = new RadGenItemRenderer()) : renderer; } }, RadGenContent.ITEM.get());
    }
    private RadGenClient() {}
}
