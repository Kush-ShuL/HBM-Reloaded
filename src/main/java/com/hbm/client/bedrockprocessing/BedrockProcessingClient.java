package com.hbm.client.bedrockprocessing;

import com.hbm.item.bedrockprocessing.*;
import net.minecraft.client.renderer.item.ItemProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class BedrockProcessingClient {
    @SubscribeEvent public static void colors(RegisterColorHandlersEvent.Item event){event.register((stack,pass)->0xff000000|(stack.getItem() instanceof ProcessedBedrockItem item?item.color(stack,pass):((LegacyBedrockItem)stack.getItem()).color(stack,pass)),BedrockProcessingContent.allItems().toArray(net.minecraft.world.item.Item[]::new));}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->ItemProperties.register(BedrockProcessingContent.ORE.get(),BedrockProcessingContent.id("legacy_metadata"),(stack,level,entity,seed)->BedrockProcessingData.canonical(BedrockProcessingData.metadata(stack))));}
    private BedrockProcessingClient(){}
}
