package com.hbm.client.radiationcleanup;

import com.hbm.machine.radiationcleanup.*;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/** Native1.21.1 item overrides select the original4 full-cube sprites using exact source modulo metadata. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RadiationCleanupClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->ItemProperties.register(RadiationCleanupContent.ABSORBER_ITEM.get(),ResourceLocation.parse("hbm:legacy_metadata"),(stack,level,entity,seed)->AbsorberBlock.rectify(RadiationCleanupContent.metadata(stack))));}
    private RadiationCleanupClient(){}
}
