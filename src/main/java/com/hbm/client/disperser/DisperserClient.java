package com.hbm.client.disperser;
import com.hbm.item.disperser.DisperserContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class DisperserClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(DisperserContent.ENTITY.get(),DisperserRenderer::new);}
    @SubscribeEvent public static void colors(RegisterColorHandlersEvent.Item event){for(var holder:java.util.List.of(DisperserContent.CANISTER,DisperserContent.GLAND)){var item=holder.get();event.register((stack,pass)->0xFF000000|item.color(stack,pass),item);}}
    private DisperserClient(){}
}
