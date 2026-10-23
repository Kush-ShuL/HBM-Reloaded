package com.hbm.client.radiolysis;
import com.hbm.machine.radiolysis.*;import net.neoforged.api.distmarker.Dist;import net.neoforged.bus.api.SubscribeEvent;import net.neoforged.fml.common.EventBusSubscriber;import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RadiolysisClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(RadiolysisContent.ENTITY.get(),RadiolysisRenderer::new);}
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(RadiolysisContent.MENU.get(),RadiolysisScreen::new);}
 @SubscribeEvent public static void extensions(net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent event){event.registerItem(new net.neoforged.neoforge.client.extensions.common.IClientItemExtensions(){private RadiolysisItemRenderer renderer;@Override public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new RadiolysisItemRenderer();return renderer;}},RadiolysisContent.ITEM.get());}
 private RadiolysisClient(){}
}
