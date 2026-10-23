package com.hbm.client.supercomputer;
import com.hbm.machine.supercomputer.SuperComputerContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class SuperComputerClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(SuperComputerContent.ENTITY.get(),SuperComputerRenderer::new);}@SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(SuperComputerContent.MENU.get(),SuperComputerScreen::new);}@SubscribeEvent public static void items(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private SuperComputerItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new SuperComputerItemRenderer()):renderer;}},SuperComputerContent.ITEM.get());}
 private SuperComputerClient(){}
}
