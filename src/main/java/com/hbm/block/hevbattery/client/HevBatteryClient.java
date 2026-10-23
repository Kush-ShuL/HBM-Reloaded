package com.hbm.block.hevbattery.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class HevBatteryClient {
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event) {
        event.getModels().replaceAll((key, model) -> {
            if (!key.id().getNamespace().equals("hbm") || !key.id().getPath().equals("hev_battery")) return model;
            return new HevBatteryModel(model, key.getVariant().equals("inventory"), event.getTextureGetter());
        });
    }
    private HevBatteryClient() {}
}
