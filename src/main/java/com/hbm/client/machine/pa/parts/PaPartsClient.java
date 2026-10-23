package com.hbm.client.machine.pa.parts;
import com.hbm.machine.pa.parts.PaPartsContent;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class PaPartsClient{
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->ItemProperties.register(PaPartsContent.COIL.get(),ResourceLocation.parse("hbm:legacy_metadata"),(stack,level,entity,seed)->PaPartsContent.COIL.get().ordinal(stack)));}private PaPartsClient(){}
}
