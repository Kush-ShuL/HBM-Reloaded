package com.hbm.client.entity.botprime;
import com.hbm.entity.mob.botprime.BotPrimeContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class BotPrimeClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(BotPrimeContent.HEAD.get(),context->new BotPrimeRenderer<>(context,true));event.registerEntityRenderer(BotPrimeContent.BODY.get(),context->new BotPrimeRenderer<>(context,false));}
}
