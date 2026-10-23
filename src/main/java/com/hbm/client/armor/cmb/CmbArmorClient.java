package com.hbm.client.armor.cmb;

import com.hbm.client.armor.plainfsb.LegacyFsbBipedClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

/** Reuse the actual original biped adapter; no copied pose/model implementation. */
@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class CmbArmorClient {
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) {
        LegacyFsbBipedClient.registerPlainBiped(event, "hbm:cmb_helmet", "hbm:cmb_plate", "hbm:cmb_legs", "hbm:cmb_boots");
    }
    private CmbArmorClient() { }
}
