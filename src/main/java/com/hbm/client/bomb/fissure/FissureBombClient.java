package com.hbm.client.bomb.fissure;
import com.hbm.bomb.fissure.FissureBombContent;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
/** Source primed appearance uses the existing ConventionalClient; only the invisible waiting carrier is new. */
@EventBusSubscriber(modid="hbm",bus=EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class FissureBombClient {
 private FissureBombClient(){}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(FissureBombContent.PENDING.get(),NoopRenderer::new);}
}
