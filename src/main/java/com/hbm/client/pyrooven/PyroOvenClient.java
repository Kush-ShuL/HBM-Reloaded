package com.hbm.client.pyrooven;
import com.hbm.machine.pyrooven.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class PyroOvenClient {
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{PyroOvenEntity.CLIENT_TICK=PyroOvenEffects::tick;PyroOvenEntity.CLIENT_REMOVED=PyroOvenEffects::removed;});}
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(PyroOvenContent.MENU.get(),PyroOvenScreen::new);}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(PyroOvenContent.ENTITY.get(),PyroOvenRenderer::new);}
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private PyroOvenItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new PyroOvenItemRenderer()):renderer;}},PyroOvenContent.ITEM.get());}
}
