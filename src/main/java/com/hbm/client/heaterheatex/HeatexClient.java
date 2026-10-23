package com.hbm.client.heaterheatex;
import com.hbm.machine.heaterheatex.HeatexContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class HeatexClient {
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(HeatexContent.MENU.get(),HeatexScreen::new);}@SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(HeatexContent.ENTITY.get(),HeatexRenderer::new);}@SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private HeatexItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new HeatexItemRenderer()):renderer;}},HeatexContent.ITEM.get());}
}
