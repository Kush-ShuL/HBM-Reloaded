package com.hbm.explosion;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Shared packet registration for the original VNT effects and player impulses. */
@EventBusSubscriber(modid="hbm",bus=EventBusSubscriber.Bus.MOD)
public final class ExplosionSupport {
 private ExplosionSupport(){}
 @SubscribeEvent public static void register(RegisterPayloadHandlersEvent event){ExplosionKnockback.register(event);ExplosionVisuals.register(event);}
}
