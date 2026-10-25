package com.hbm.compat.computercraft.media;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

/** Reuse CC's real disk layers, keeping the source cyan program-disk colour. */
@EventBusSubscriber(modid="hbm",bus=EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class PwranglerMediaClient {
    @SubscribeEvent public static void colors(RegisterColorHandlersEvent.Item event){if(PwranglerMedia.available())event.register((stack,layer)->layer==1?0xFF66FFFF:0xFFFFFFFF,PwranglerMedia.item());}
    private PwranglerMediaClient(){}
}
