package com.hbm.client.entity.crab;
import com.hbm.entity.mob.crab.CyberCrabContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class CrabClient{
 private CrabClient(){}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(CyberCrabContent.CYBER.get(),context->new CrabRenderer(context,false));event.registerEntityRenderer(CyberCrabContent.TESLA.get(),context->new CrabRenderer(context,true));}
}
