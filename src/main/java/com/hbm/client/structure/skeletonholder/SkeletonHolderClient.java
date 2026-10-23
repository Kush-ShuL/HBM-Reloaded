package com.hbm.client.structure.skeletonholder;
import com.hbm.block.skeletonholder.SkeletonHolderContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
/** Original source has no item renderer: the item remains the real soul-sand icon. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class SkeletonHolderClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(SkeletonHolderContent.ENTITY.get(),SkeletonHolderRenderer::new);}
 private SkeletonHolderClient(){}
}
