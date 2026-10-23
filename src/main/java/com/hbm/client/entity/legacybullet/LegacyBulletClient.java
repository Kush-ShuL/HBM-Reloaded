package com.hbm.client.entity.legacybullet;

import com.hbm.client.entity.crab.CyberTauRenderer;
import com.hbm.entity.projectile.legacy.LegacyBulletContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid="hbm",bus=EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class LegacyBulletClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(LegacyBulletContent.BULLET.get(),CyberTauRenderer::new);}
    private LegacyBulletClient(){}
}
