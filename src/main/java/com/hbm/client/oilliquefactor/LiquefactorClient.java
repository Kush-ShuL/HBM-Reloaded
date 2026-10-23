package com.hbm.client.oilliquefactor;
import com.hbm.machine.oilliquefactor.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class LiquefactorClient {
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent e){e.register(LiquefactorContent.MENU.get(),LiquefactorScreen::new);}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers e){e.registerBlockEntityRenderer(LiquefactorContent.ENTITY.get(),LiquefactorRenderer::new);}
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent e){e.registerItem(new IClientItemExtensions(){private LiquefactorItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new LiquefactorItemRenderer();return renderer;}},LiquefactorContent.ITEM.get());}
}
