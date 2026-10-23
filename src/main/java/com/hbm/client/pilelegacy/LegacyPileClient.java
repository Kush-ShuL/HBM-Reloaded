package com.hbm.client.pilelegacy;

import com.hbm.client.bomb.nuclear.LegacyExplosionEffects;
import com.hbm.machine.pilelegacy.LegacyPileSmoke;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/** Source ordinary cube models have no TESR or separate item renderer. Only the original smoke particle dispatch is adapted. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class LegacyPileClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->LegacyPileSmoke.client=packet->LegacyExplosionEffects.particle("smoke",packet.x(),packet.y(),packet.z(),0,.05,0));}
}
