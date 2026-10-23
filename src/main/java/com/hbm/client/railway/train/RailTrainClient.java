package com.hbm.client.railway.train;
import com.hbm.railway.train.*;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RailTrainClient {
    @SubscribeEvent public static void renderer(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(RailTrainContent.TRAM.get(),RailTrainRenderer::new);event.registerEntityRenderer(RailTrainContent.TRAILER.get(),RailTrainRenderer::new);event.registerEntityRenderer(RailTrainContent.BOUNDS.get(),NoopRenderer::new);event.registerEntityRenderer(RailTrainContent.SEAT.get(),NoopRenderer::new);}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(RailTrainContent.TRAM_MENU.get(),RailTrainScreen::new);event.register(RailTrainContent.TRAILER_MENU.get(),RailTrainScreen::new);}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->ItemProperties.register(RailTrainContent.TRAIN.get(),RailTrainContent.id("legacy_metadata"),(stack,level,entity,seed)->SourceTrainItem.ordinal(stack)));}
    private RailTrainClient(){}
}
