package com.hbm.client.soyuz;

import com.hbm.hazard.HbmHazards;
import com.hbm.world.soyuz.SoyuzCapsulesContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class SoyuzCapsulesClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(SoyuzCapsulesContent.BLOCK_ENTITY.get(), SoyuzCapsuleRenderer::new);
        event.registerEntityRenderer(SoyuzCapsulesContent.DESCENT.get(), SoyuzDescentRenderer::new);
    }
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event) { event.register(SoyuzCapsulesContent.MENU.get(), SoyuzCapsuleScreen::new); }
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private SoyuzCapsuleItemRenderer renderer;
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() { return renderer == null ? renderer = new SoyuzCapsuleItemRenderer() : renderer; }
        }, SoyuzCapsulesContent.ITEM.get());
    }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(SoyuzCapsulesContent.SOYUZ.get(), ResourceLocation.parse("hbm:legacy_metadata"),
                (stack, level, entity, seed) -> Math.max(0, Math.min(2, stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0)))));
    }
    private SoyuzCapsulesClient() { }
}
