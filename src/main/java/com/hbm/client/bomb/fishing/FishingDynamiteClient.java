package com.hbm.client.bomb.fishing;
import com.hbm.bomb.fishing.FishingDynamiteContent;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
/** Legacy RenderManager inherited RenderEntityItem for the buoyant subtype; same native superclass renderer here. */
@EventBusSubscriber(modid="hbm",bus=EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class FishingDynamiteClient {
 private FishingDynamiteClient(){}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(FishingDynamiteContent.BUOYANT.get(),ItemEntityRenderer::new);event.registerEntityRenderer(FishingDynamiteContent.PENDING.get(),NoopRenderer::new);}
}
