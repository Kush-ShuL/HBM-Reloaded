package com.hbm.client.silex;
import com.hbm.machine.silex.SilexContent;import net.neoforged.api.distmarker.Dist;import net.neoforged.bus.api.SubscribeEvent;import net.neoforged.fml.common.EventBusSubscriber;import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class SilexClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(SilexContent.ENTITY.get(),SilexRenderer::new);}@SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(SilexContent.MENU.get(),SilexScreen::new);}@SubscribeEvent public static void extensions(net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent event){event.registerItem(new net.neoforged.neoforge.client.extensions.common.IClientItemExtensions(){private SilexItemRenderer renderer;@Override public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new SilexItemRenderer();return renderer;}},SilexContent.ITEM.get());}private SilexClient(){}
}
