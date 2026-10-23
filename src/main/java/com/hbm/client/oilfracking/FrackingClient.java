package com.hbm.client.oilfracking;
import com.hbm.machine.oilfracking.FrackingContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class FrackingClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers e){e.registerBlockEntityRenderer(FrackingContent.ENTITY.get(),FrackingRenderer::new);}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent e){e.register(FrackingContent.MENU.get(),FrackingScreen::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent e){e.registerItem(new IClientItemExtensions(){private FrackingItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new FrackingItemRenderer()):renderer;}},FrackingContent.ITEM.get());}
}
