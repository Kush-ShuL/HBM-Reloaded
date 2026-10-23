package com.hbm.block.pipe.client.decorative;

import com.hbm.HbmReloaded;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class DecorativePipeClient {
    private DecorativePipeClient() {}
    @SubscribeEvent public static void loaders(ModelEvent.RegisterGeometryLoaders event) { event.register(HbmReloaded.id("decorative_pipe"),DecorativePipeGeometry.LOADER); }
}
