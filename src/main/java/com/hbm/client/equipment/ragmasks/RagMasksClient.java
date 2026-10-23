package com.hbm.client.equipment.ragmasks;

import com.hbm.client.armor.plainfsb.LegacyFsbBipedClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

/** Reuse the real old plain ModArmor biped, with the original masks' two armor skins. */
@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class RagMasksClient {
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) {
        LegacyFsbBipedClient.registerPlainBiped(event, "hbm:mask_rag", "hbm:mask_piss");
    }
    private RagMasksClient() { }
}
