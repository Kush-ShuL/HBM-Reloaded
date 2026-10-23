package com.hbm.world.structure.materials;

import com.hbm.HbmReloaded;
import com.hbm.hazard.HbmHazards;
import net.minecraft.client.renderer.item.ItemProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid=HbmReloaded.MOD_ID,value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class MeteorMaterialClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->ItemProperties.register(MeteorMaterialContent.COLORED_ITEM.get(),HbmReloaded.id("legacy_metadata"),(stack,level,entity,seed)->Math.floorMod(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0),16)));}
    private MeteorMaterialClient(){}
}
