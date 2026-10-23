package com.hbm.block.door.client;

import com.hbm.block.door.SourceDoorContent;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class SourceDoorClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->SourceDoorContent.DOOR_ITEMS.forEach(item->ItemBlockRenderTypes.setRenderLayer(item.get().getBlock(),RenderType.cutout())));}
}
