package com.hbm.client.armor.dnt;

import com.hbm.client.armor.plainfsb.LegacyFsbBipedClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class DntArmorClient {
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) {
        LegacyFsbBipedClient.registerPlainBiped(event, "hbm:dnt_helmet", "hbm:dnt_plate", "hbm:dnt_legs", "hbm:dnt_boots");
    }
    private DntArmorClient() { }
}
