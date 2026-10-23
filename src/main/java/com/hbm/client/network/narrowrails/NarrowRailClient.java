package com.hbm.client.network.narrowrails;
import com.hbm.network.narrowrails.NarrowRailContent;
import net.minecraft.client.renderer.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class NarrowRailClient {
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->NarrowRailContent.RAILS.values().forEach(block->ItemBlockRenderTypes.setRenderLayer(block.get(),RenderType.cutout())));}
 private NarrowRailClient(){}
}
