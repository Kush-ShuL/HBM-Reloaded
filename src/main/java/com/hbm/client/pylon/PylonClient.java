package com.hbm.client.pylon;

import com.hbm.block.pylon.PylonContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class PylonClient {
    private PylonClient() {}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerBlockEntityRenderer(PylonContent.ENTITY.get(),PylonRenderer::new); }
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) {
        var extension=new IClientItemExtensions() {
            private PylonItemRenderer renderer;
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() { if(renderer==null) renderer=new PylonItemRenderer();return renderer; }
        };
        for(var holder:PylonContent.ITEMS_BY_KIND.values()) event.registerItem(extension,holder.get());
    }
}
