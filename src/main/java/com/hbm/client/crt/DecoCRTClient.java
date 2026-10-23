package com.hbm.client.crt;

import com.hbm.block.crt.DecoCRTContent;
import com.hbm.block.crt.DecoCRTItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid="hbm", value=Dist.CLIENT, bus=EventBusSubscriber.Bus.MOD)
public final class DecoCRTClient {
    private DecoCRTClient() {}
    @SubscribeEvent public static void loaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(ResourceLocation.parse("hbm:deco_crt"), DecoCRTGeometry.LOADER);
    }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(DecoCRTContent.ITEM.get(), ResourceLocation.parse("hbm:legacy_metadata"),
                (stack, level, entity, seed) -> DecoCRTItem.variant(stack)));
    }
}
