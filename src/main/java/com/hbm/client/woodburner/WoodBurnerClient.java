package com.hbm.client.woodburner;

import com.hbm.machine.woodburner.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class WoodBurnerClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerBlockEntityRenderer(WoodBurnerContent.ENTITY.get(),WoodBurnerRenderer::new); }
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) { event.registerItem(new IClientItemExtensions() {
        private WoodBurnerItemRenderer renderer;
        @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() { if(renderer==null) renderer=new WoodBurnerItemRenderer(); return renderer; }
    },WoodBurnerContent.ITEM.get()); }
}
