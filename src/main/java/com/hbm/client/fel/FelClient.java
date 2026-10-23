package com.hbm.client.fel;
import com.hbm.machine.fel.*;import net.neoforged.api.distmarker.Dist;import net.neoforged.bus.api.SubscribeEvent;import net.neoforged.fml.common.EventBusSubscriber;import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class FelClient {
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{FelBlockEntity.CLIENT_TICK=FelSound::tick;FelBlockEntity.CLIENT_REMOVED=FelSound::removed;});}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(FelContent.ENTITY.get(),FelRenderer::new);}@SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(FelContent.MENU.get(),FelScreen::new);}
 @SubscribeEvent public static void extensions(net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent event){event.registerItem(new net.neoforged.neoforge.client.extensions.common.IClientItemExtensions(){private FelItemRenderer renderer;@Override public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new FelItemRenderer();return renderer;}},FelContent.ITEM.get());}
 private FelClient(){}
}
