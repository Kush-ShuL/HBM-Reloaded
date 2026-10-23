package com.hbm.client.centrifuge;

import com.hbm.machine.centrifuge.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class CentrifugeClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) { event.enqueueWork(()->{ CentrifugeBlockEntity.CLIENT_TICK=CentrifugeSound::clientTick; CentrifugeBlockEntity.CLIENT_REMOVED=CentrifugeSound::removed; }); }
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerBlockEntityRenderer(CentrifugeContent.ENTITY.get(),CentrifugeRenderer::new); }
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) { event.registerItem(new IClientItemExtensions() {
        private CentrifugeItemRenderer renderer;
        @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() { if(renderer==null) renderer=new CentrifugeItemRenderer(); return renderer; }
    },CentrifugeContent.ITEM.get()); }
}
