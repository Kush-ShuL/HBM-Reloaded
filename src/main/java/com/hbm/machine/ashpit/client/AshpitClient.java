package com.hbm.machine.ashpit.client;
import com.hbm.machine.ashpit.AshpitContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class AshpitClient {
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(AshpitContent.MENU.get(),AshpitScreen::new);}@SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(AshpitContent.ENTITY.get(),AshpitRenderer::new);}@SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private AshpitItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new AshpitItemRenderer()):renderer;}},AshpitContent.ITEM.get());}
}
