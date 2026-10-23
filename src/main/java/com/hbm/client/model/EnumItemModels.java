package com.hbm.client.model;

import com.hbm.HbmReloaded;
import com.hbm.registry.HbmEnumItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = HbmReloaded.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class EnumItemModels {
    private EnumItemModels() {}

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> { HbmEnumItems.all().forEach(holder -> {
            var item = holder.get();
            ItemProperties.register(item, HbmReloaded.id("legacy_metadata"), (stack, level, entity, seed) -> item.ordinal(stack));
        });
            com.hbm.bomb.nuclear.NuclearContent.BREEDING_RODS.values().forEach(holder -> {
                var item = holder.get();
                ItemProperties.register(item, HbmReloaded.id("legacy_metadata"), (stack, level, entity, seed) -> item.ordinal(stack));
            });
        });
    }
}
