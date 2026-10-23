package com.hbm.client.cokestorage;

import com.hbm.block.cokestorage.CokeStorageContent;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/** Standard legacy-metadata item-model property, identical to the sole LegacyOreItem contract. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class CokeStorageClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{
        var item=CokeStorageContent.BLOCK_COKE_ITEM.get();
        ItemProperties.register(item,ResourceLocation.fromNamespaceAndPath("hbm","legacy_metadata"),(stack,level,entity,seed)->item.ordinal(stack));
    });}
    private CokeStorageClient(){}
}
