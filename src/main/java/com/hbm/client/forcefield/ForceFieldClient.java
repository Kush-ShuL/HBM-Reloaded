package com.hbm.client.forcefield;
import com.hbm.machine.forcefield.*;import net.neoforged.api.distmarker.Dist;import net.neoforged.bus.api.SubscribeEvent;import net.neoforged.fml.common.EventBusSubscriber;import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ForceFieldClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(ForceFieldContent.ENTITY.get(),ForceFieldRenderer::new);}
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(ForceFieldContent.MENU.get(),ForceFieldScreen::new);}
 @SubscribeEvent public static void extensions(net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent event){event.registerItem(new net.neoforged.neoforge.client.extensions.common.IClientItemExtensions(){private ForceFieldItemRenderer renderer;@Override public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new ForceFieldItemRenderer();return renderer;}},ForceFieldContent.ITEM.get());}
 private ForceFieldClient(){}
}
