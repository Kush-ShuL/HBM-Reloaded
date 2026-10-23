package com.hbm.client.taint;
import com.hbm.world.taint.TaintContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class TaintClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(TaintContent.CREEPER.get(),TaintedCreeperRenderer::new);event.registerEntityRenderer(TaintContent.CRAB.get(),TaintCrabRenderer::new);}
 private TaintClient(){}
}
