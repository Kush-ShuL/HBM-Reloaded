package com.hbm.client.cyclotron;
import com.hbm.machine.cyclotron.CyclotronContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class CyclotronClient {
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent e){e.register(CyclotronContent.MENU.get(),CyclotronScreen::new);}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers e){e.registerBlockEntityRenderer(CyclotronContent.ENTITY.get(),CyclotronRenderer::new);}
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent e){e.registerItem(new IClientItemExtensions(){private CyclotronItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new CyclotronItemRenderer();return renderer;}},CyclotronContent.ITEM.get());}
}
