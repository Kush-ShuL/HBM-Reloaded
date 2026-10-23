package com.hbm.client;

import com.hbm.HbmReloaded;
import com.hbm.item.UndefinedNameState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = HbmReloaded.MOD_ID, value = Dist.CLIENT)
public final class CustomLoreEvents {
    private CustomLoreEvents() {}

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        UndefinedNameState.NAME.updateTick(UndefinedNameState.NAMES);
    }
}
