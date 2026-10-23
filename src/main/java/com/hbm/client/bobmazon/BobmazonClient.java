package com.hbm.client.bobmazon;

import com.hbm.bobmazon.*;
import com.hbm.client.bomb.nuclear.LegacyRocketFlameParticle;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class BobmazonClient {
 private static final java.util.Random EXHAUST_RANDOM=new java.util.Random();
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{
  BobmazonContent.CLIENT_OPEN=(player,hand)->Minecraft.getInstance().setScreen(new BobmazonScreen(player,hand));
  BobmazonContent.CLIENT_EXHAUST=entity->{var minecraft=Minecraft.getInstance();if(minecraft.level!=null){double x=entity.getX()+EXHAUST_RANDOM.nextGaussian()*0,y=entity.getY()+1+EXHAUST_RANDOM.nextGaussian()*0,z=entity.getZ()+EXHAUST_RANDOM.nextGaussian()*0;minecraft.particleEngine.add(new LegacyRocketFlameParticle(minecraft.level,x,y,z));}};
 });}
 @SubscribeEvent public static void entities(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(BobmazonContent.DELIVERY.get(),BobmazonRenderer::new);}
 @SubscribeEvent public static void colors(RegisterColorHandlersEvent.Item event){event.register((stack,pass)->0xff000000|CustomKitItem.color(stack,pass),BobmazonContent.KIT.get());}
 private BobmazonClient(){}
}
