package com.hbm.client.solidifier;
import com.hbm.machine.solidifier.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class SolidifierClient {
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent e){e.register(SolidifierContent.MENU.get(),SolidifierScreen::new);}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers e){e.registerBlockEntityRenderer(SolidifierContent.ENTITY.get(),SolidifierRenderer::new);}
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent e){e.registerItem(new IClientItemExtensions(){private SolidifierItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new SolidifierItemRenderer();return renderer;}},SolidifierContent.ITEM.get());}
}
