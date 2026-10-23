package com.hbm.client.press;

import com.hbm.machine.press.PressContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class PressClient {
    private PressClient() {}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerBlockEntityRenderer(PressContent.ENTITY.get(),PressRenderer::new); }
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) { event.registerItem(new IClientItemExtensions() {
        private PressItemRenderer renderer;
        @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() { if(renderer==null) renderer=new PressItemRenderer(); return renderer; }
    },PressContent.ITEM.get()); }
}
