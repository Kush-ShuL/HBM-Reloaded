package com.hbm.client.satellite.attack;
import com.hbm.satellite.attack.AttackSatelliteContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class AttackSatelliteClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(AttackSatelliteContent.DEATH_BLAST.get(),DeathBlastRenderer::new);event.registerEntityRenderer(AttackSatelliteContent.ORBITAL_LASER.get(),OrbitalLaserRenderer::new);}
 private AttackSatelliteClient(){}
}
