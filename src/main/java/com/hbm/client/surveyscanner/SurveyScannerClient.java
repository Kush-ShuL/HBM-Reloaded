package com.hbm.client.surveyscanner;

import com.hbm.client.reactor.pwr.PwrMarkers;
import com.hbm.item.surveyscanner.SurveyScannerMarker;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/** Uses the sole existing source RenderOverhead box/text path; no extra world render listener. */
@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class SurveyScannerClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) { event.enqueueWork(() ->
            SurveyScannerMarker.CLIENT = packet -> PwrMarkers.add(packet.position(), packet.labelKey().isEmpty()?null:Component.translatable(packet.labelKey()), packet.color(), packet.expires(), packet.distance())); }
    private SurveyScannerClient() { }
}
