package com.hbm.client.reactor.watz.fuel;

import com.hbm.machine.reactor.watz.fuel.*;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class WatzFuelClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{for(var item:new net.minecraft.world.item.Item[]{WatzFuelContent.FRESH.get(),WatzFuelContent.DEPLETED.get()})ItemProperties.register(item,ResourceLocation.parse("hbm:legacy_metadata"),(stack,level,owner,seed)->WatzPelletItem.metadata(stack));});}
    private WatzFuelClient(){}
}
