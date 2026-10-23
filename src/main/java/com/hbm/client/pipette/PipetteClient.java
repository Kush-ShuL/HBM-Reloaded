package com.hbm.client.pipette;

import com.hbm.item.pipette.PipetteContent;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

/** Source base icon plus a tintable full/empty overlay, using native item models. */
@EventBusSubscriber(modid="hbm",bus=EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class PipetteClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{
        for(var holder:PipetteContent.PIPETTES.values())ItemProperties.register(holder.get(),ResourceLocation.parse("hbm:pipette_filled"),(stack,level,living,seed)->holder.get().getFill(stack)>0?1:0);
    });}
    @SubscribeEvent public static void colors(RegisterColorHandlersEvent.Item event){
        for(var holder:PipetteContent.PIPETTES.values())event.register((stack,pass)->holder.get().color(stack,pass),holder.get());
    }
    private PipetteClient(){}
}
