package com.hbm.client.armor.wd40;

import com.hbm.armor.wd40.Wd40Content;
import com.hbm.client.entity.crab.CrabParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/** Actual ClientProxy vanillaExt/reddust(.01,.5,.8),using the existing original1.7 dust particle. */
@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class Wd40Client {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) { event.enqueueWork(() ->
        Wd40Content.CLIENT_DUST = (level, pos) -> {
            if (level instanceof ClientLevel client) CrabParticles.colored(client, pos, .01F, .5F, .8F);
        }); }
    private Wd40Client() { }
}
