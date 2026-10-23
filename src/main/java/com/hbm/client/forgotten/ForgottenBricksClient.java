package com.hbm.client.forgotten;

import com.hbm.block.forgotten.ForgottenBricksContent;
import com.hbm.hazard.HbmHazards;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/** ItemBlockBase passes raw icon metadata; world nibble and normal-drop rectification remain in the shared source item/block core. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ForgottenBricksClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{for(var item:ForgottenBricksContent.indexedItems().values())ItemProperties.register(item,ResourceLocation.parse("hbm:legacy_metadata"),(stack,level,entity,seed)->stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0));});}
    private ForgottenBricksClient(){}
}
