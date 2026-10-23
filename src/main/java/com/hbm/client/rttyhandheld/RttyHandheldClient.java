package com.hbm.client.rttyhandheld;

import com.hbm.item.rttyhandheld.RttyHandheldItem;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class RttyHandheldClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> RttyHandheldItem.CLIENT_OPEN = (hand, kind) -> Minecraft.getInstance().setScreen(new RttyChannelScreen(hand, kind)));
    }
    private RttyHandheldClient() { }
}
