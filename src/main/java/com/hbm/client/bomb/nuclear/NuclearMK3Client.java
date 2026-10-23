package com.hbm.client.bomb.nuclear;
import com.hbm.bomb.nuclear.NuclearContent;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class NuclearMK3Client {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(NuclearContent.MK3.get(),NoopRenderer::new);event.registerEntityRenderer(NuclearContent.FLEIJA_CLOUD.get(),NoopRenderer::new);event.registerEntityRenderer(NuclearContent.SOLINIUM_CLOUD.get(),NoopRenderer::new);}
 private NuclearMK3Client(){}
}
