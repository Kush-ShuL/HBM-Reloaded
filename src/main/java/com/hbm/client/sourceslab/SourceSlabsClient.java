package com.hbm.client.sourceslab;

import com.hbm.HbmReloaded;
import com.hbm.block.masonry.LegacyMultiSlabBlock;
import com.hbm.block.sourceslab.SourceSlabsContent;
import com.hbm.hazard.HbmHazards;
import net.minecraft.client.renderer.item.ItemProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class SourceSlabsClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> SourceSlabsContent.stoneItems().forEach(item ->
            ItemProperties.register(item, HbmReloaded.id("legacy_metadata"), (stack, level, entity, seed) ->
                ((LegacyMultiSlabBlock)item.getBlock()).material(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0)))));
    }
    private SourceSlabsClient() {}
}
