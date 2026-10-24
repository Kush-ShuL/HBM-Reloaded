package com.hbm.client.dye;

import com.hbm.item.dye.ChemicalDyeContent;
import com.hbm.item.dye.ChemicalDyeItem;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class ChemicalDyeClient {
    @SubscribeEvent
    public static void colors(RegisterColorHandlersEvent.Item event) {
        event.register(ChemicalDyeItem::color, ChemicalDyeContent.items().toArray(Item[]::new));
    }

    private ChemicalDyeClient() { }
}
