package com.hbm.worldgen.bedrockresource.client;

import com.hbm.item.bedrockresource.RawBedrockOreItem;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class BedrockResourceClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->RawBedrockOreItem.CREATIVE_POSITION=()->{var player=Minecraft.getInstance().player;return player==null?null:player.blockPosition();});}
    private BedrockResourceClient(){}
}
