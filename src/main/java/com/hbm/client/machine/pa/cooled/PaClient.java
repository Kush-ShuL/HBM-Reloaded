package com.hbm.client.machine.pa.cooled;
import com.hbm.machine.pa.cooled.PaContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class PaClient{
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(PaContent.ENTITY.get(),PaRenderer::new);}@SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(PaContent.MENU.get(),PaScreen::new);}@SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private PaItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new PaItemRenderer()):renderer;}},PaContent.ITEM.values().stream().map(value->value.get()).toArray(net.minecraft.world.item.Item[]::new));}private PaClient(){}
}
