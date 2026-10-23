package com.hbm.client.oreslopper;
import com.hbm.machine.oreslopper.*;import net.neoforged.api.distmarker.Dist;import net.neoforged.bus.api.SubscribeEvent;import net.neoforged.fml.common.EventBusSubscriber;import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class OreSlopperClient {
 @SubscribeEvent public static void setup(net.neoforged.fml.event.lifecycle.FMLClientSetupEvent event){event.enqueueWork(()->OreSlopperBlockEntity.CLIENT_TICK=OreSlopperAnimation::tick);}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(OreSlopperContent.ENTITY.get(),OreSlopperRenderer::new);}
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(OreSlopperContent.MENU.get(),OreSlopperScreen::new);}
 @SubscribeEvent public static void extensions(net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent event){event.registerItem(new net.neoforged.neoforge.client.extensions.common.IClientItemExtensions(){private OreSlopperItemRenderer renderer;@Override public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new OreSlopperItemRenderer();return renderer;}},OreSlopperContent.ITEM.get());}
 private OreSlopperClient(){}
}
