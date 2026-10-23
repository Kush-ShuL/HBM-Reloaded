package com.hbm.client.entity.hunterchopper;
import com.hbm.entity.mob.hunterchopper.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class HunterChopperClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers e){e.registerEntityRenderer(HunterChopperContent.HUNTER_CHOPPER.get(),HunterChopperRenderer::new);e.registerEntityRenderer(HunterChopperContent.MINE.get(),ChopperMineRenderer::new);}
 @SubscribeEvent public static void setup(FMLClientSetupEvent e){e.enqueueWork(()->{HunterChopper.CLIENT_TICK=ChopperClientEffects::tick;HunterChopper.CLIENT_REMOVE=ChopperClientEffects::removed;ChopperMine.CLIENT_TICK=ChopperClientEffects::tick;ChopperMine.CLIENT_REMOVE=ChopperClientEffects::removed;});}
}
