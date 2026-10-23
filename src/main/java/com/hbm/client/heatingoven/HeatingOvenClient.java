package com.hbm.client.heatingoven;
import com.hbm.machine.heatingoven.HeatingOvenContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class HeatingOvenClient {
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(HeatingOvenContent.MENU.get(),HeatingOvenScreen::new);}@SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(HeatingOvenContent.ENTITY.get(),HeatingOvenRenderer::new);}@SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private HeatingOvenItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new HeatingOvenItemRenderer()):renderer;}},HeatingOvenContent.ITEM.get());}
}
