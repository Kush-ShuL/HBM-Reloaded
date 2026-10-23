package com.hbm.client.oilmixer;
import com.hbm.machine.oilmixer.MixerContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class MixerClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers e){e.registerBlockEntityRenderer(MixerContent.ENTITY.get(),MixerRenderer::new);}@SubscribeEvent public static void screens(RegisterMenuScreensEvent e){e.register(MixerContent.MENU.get(),MixerScreen::new);}@SubscribeEvent public static void extensions(RegisterClientExtensionsEvent e){e.registerItem(new IClientItemExtensions(){private MixerItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new MixerItemRenderer()):renderer;}},MixerContent.ITEM.get());}
}
