package com.hbm.client.soldering;
import com.hbm.machine.soldering.SolderingContent;import net.neoforged.api.distmarker.Dist;import net.neoforged.bus.api.SubscribeEvent;import net.neoforged.fml.common.EventBusSubscriber;import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class SolderingClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(SolderingContent.ENTITY.get(),SolderingRenderer::new);}
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(SolderingContent.MENU.get(),SolderingScreen::new);}
 @SubscribeEvent public static void extensions(net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent event){event.registerItem(new net.neoforged.neoforge.client.extensions.common.IClientItemExtensions(){private SolderingItemRenderer renderer;@Override public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new SolderingItemRenderer();return renderer;}},SolderingContent.ITEM.get());}
 private SolderingClient(){}
}
