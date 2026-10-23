package com.hbm.client.largeturbine;
import com.hbm.machine.largeturbine.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class LargeTurbineClient {
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{LargeTurbineEntity.clientTick=LargeTurbineSound::tick;LargeTurbineEntity.clientRemoved=LargeTurbineSound::removed;});}
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(LargeTurbineContent.MENU.get(),LargeTurbineScreen::new);}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(LargeTurbineContent.ENTITY.get(),LargeTurbineRenderer::new);}
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private LargeTurbineItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new LargeTurbineItemRenderer();return renderer;}},LargeTurbineContent.ITEM.get());}
}
