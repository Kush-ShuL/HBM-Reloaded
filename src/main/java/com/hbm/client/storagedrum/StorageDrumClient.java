package com.hbm.client.storagedrum;

import com.hbm.machine.storagedrum.StorageDrumContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class StorageDrumClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerBlockEntityRenderer(StorageDrumContent.ENTITY.get(), StorageDrumRenderer::new); }
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event) { event.register(StorageDrumContent.MENU.get(), StorageDrumScreen::new); }
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private StorageDrumItemRenderer renderer;
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() { return renderer == null ? (renderer = new StorageDrumItemRenderer()) : renderer; }
        }, StorageDrumContent.ITEM.get());
    }
    private StorageDrumClient() {}
}
