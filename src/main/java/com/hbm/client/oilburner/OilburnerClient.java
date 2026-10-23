package com.hbm.client.oilburner;
import com.hbm.machine.oilburner.OilburnerContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class OilburnerClient {
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(OilburnerContent.MENU.get(),OilburnerScreen::new);}@SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(OilburnerContent.ENTITY.get(),OilburnerRenderer::new);}@SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private OilburnerItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new OilburnerItemRenderer()):renderer;}},OilburnerContent.ITEM.get());}
}
