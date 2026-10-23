package com.hbm.client.bottlecap;

import com.hbm.block.bottlecap.BottleCapContent;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/** Same sole metadata property/LegacyOreItem subtype contract used by existing enum storage blocks. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class BottleCapClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{
        var item=BottleCapContent.ITEM.get();
        ItemProperties.register(item,ResourceLocation.fromNamespaceAndPath("hbm","legacy_metadata"),
            (stack,level,entity,seed)->item.ordinal(stack));
    });}
    private BottleCapClient(){}
}
