package com.hbm.client.arcwelder;
import com.hbm.machine.arcwelder.ArcWelderContent;import net.neoforged.api.distmarker.Dist;import net.neoforged.bus.api.SubscribeEvent;import net.neoforged.fml.common.EventBusSubscriber;import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ArcWelderClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(ArcWelderContent.ENTITY.get(),ArcWelderRenderer::new);}
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(ArcWelderContent.MENU.get(),ArcWelderScreen::new);}
 @SubscribeEvent public static void extensions(net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent event){event.registerItem(new net.neoforged.neoforge.client.extensions.common.IClientItemExtensions(){private ArcWelderItemRenderer renderer;@Override public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new ArcWelderItemRenderer();return renderer;}},ArcWelderContent.ITEM.get());}
 private ArcWelderClient(){}
}
