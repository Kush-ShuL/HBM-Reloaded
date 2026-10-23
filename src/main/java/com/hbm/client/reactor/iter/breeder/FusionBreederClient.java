package com.hbm.client.reactor.iter.breeder;
import com.hbm.machine.reactor.iter.breeder.FusionBreederContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class FusionBreederClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(FusionBreederContent.ENTITY.get(),FusionBreederRenderer::new);}@SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(FusionBreederContent.MENU.get(),FusionBreederScreen::new);}@SubscribeEvent public static void items(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private FusionBreederItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new FusionBreederItemRenderer()):renderer;}},FusionBreederContent.ITEM.get());}private FusionBreederClient(){}
}
