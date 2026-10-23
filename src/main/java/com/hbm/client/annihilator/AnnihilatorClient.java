package com.hbm.client.annihilator;
import com.hbm.machine.annihilator.AnnihilatorContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class AnnihilatorClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers e){e.registerBlockEntityRenderer(AnnihilatorContent.ENTITY.get(),AnnihilatorRenderer::new);}@SubscribeEvent public static void screens(RegisterMenuScreensEvent e){e.register(AnnihilatorContent.MENU.get(),AnnihilatorScreen::new);}@SubscribeEvent public static void extensions(RegisterClientExtensionsEvent e){e.registerItem(new IClientItemExtensions(){private AnnihilatorItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new AnnihilatorItemRenderer()):renderer;}},AnnihilatorContent.ITEM.get());}
}
