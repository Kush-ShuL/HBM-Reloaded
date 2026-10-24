package com.hbm.client.bedrockprocessing;

import com.hbm.item.bedrockprocessing.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class BedrockProcessingClient {
    @SubscribeEvent public static void colors(RegisterColorHandlersEvent.Item event){event.register((stack,pass)->0xff000000|(stack.getItem() instanceof ProcessedBedrockItem item?item.color(pass):stack.getItem() instanceof EnrichedBedrockItem item?item.color(pass):((LegacyBedrockItem)stack.getItem()).color(pass)),BedrockProcessingContent.allItems().toArray(net.minecraft.world.item.Item[]::new));}
    private BedrockProcessingClient(){}
}
