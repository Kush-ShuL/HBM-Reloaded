package com.hbm.block.masonry.client;

import com.hbm.HbmReloaded;
import com.hbm.block.masonry.*;
import com.hbm.hazard.HbmHazards;
import net.minecraft.client.renderer.item.ItemProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class MasonryModels {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) { event.enqueueWork(() -> MasonryContent.slabItems().forEach(item -> ItemProperties.register(item,HbmReloaded.id("legacy_metadata"),(stack,world,entity,seed)->((LegacyMultiSlabBlock)item.getBlock()).material(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0))))); }
    private MasonryModels() { }
}
