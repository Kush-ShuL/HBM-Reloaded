package com.hbm.client.excavator;
import com.hbm.machine.excavator.*;import com.hbm.conveyor.ConveyorObjects;import net.neoforged.api.distmarker.Dist;import net.neoforged.bus.api.SubscribeEvent;import net.neoforged.fml.common.EventBusSubscriber;import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ExcavatorClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(ExcavatorContent.ENTITY.get(),ExcavatorRenderer::new);event.registerEntityRenderer(ConveyorObjects.ITEM.get(),MovingItemRenderer::new);}@SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(ExcavatorContent.MENU.get(),ExcavatorScreen::new);}
 @SubscribeEvent public static void extensions(net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent event){event.registerItem(new net.neoforged.neoforge.client.extensions.common.IClientItemExtensions(){private ExcavatorItemRenderer renderer;@Override public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new ExcavatorItemRenderer();return renderer;}},ExcavatorContent.ITEM.get());}private ExcavatorClient(){}
}
