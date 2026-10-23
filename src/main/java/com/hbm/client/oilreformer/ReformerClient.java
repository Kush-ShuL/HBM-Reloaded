package com.hbm.client.oilreformer;
import com.hbm.machine.oilreformer.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ReformerClient {
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent e){e.register(ReformerContent.MENU.get(),ReformerScreen::new);}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers e){e.registerBlockEntityRenderer(ReformerContent.ENTITY.get(),ReformerRenderer::new);}
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent e){e.registerItem(new IClientItemExtensions(){private ReformerItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new ReformerItemRenderer();return renderer;}},ReformerContent.ITEM.get());}
}
