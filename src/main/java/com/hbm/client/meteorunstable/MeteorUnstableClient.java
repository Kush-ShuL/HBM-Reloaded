package com.hbm.client.meteorunstable;

import com.hbm.item.meteorunstable.*;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class MeteorUnstableClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            var property = ResourceLocation.fromNamespaceAndPath("hbm", "meteor_variant");
            ItemProperties.register(MeteorUnstableContent.METASTABLE.get(), property, (stack, level, owner, seed) -> MetastableIngotItem.icon(stack) / 4F);
        });
    }
}
