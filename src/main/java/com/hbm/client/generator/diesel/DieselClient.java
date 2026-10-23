package com.hbm.client.generator.diesel;
import com.hbm.machine.generator.diesel.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class DieselClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{DieselBlockEntity.clientTick=DieselSound::tick;DieselBlockEntity.clientRemoved=DieselSound::removed;});}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(DieselContent.MENU.get(),DieselScreen::new);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(DieselContent.ENTITY.get(),DieselRenderer::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private DieselItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new DieselItemRenderer()):renderer;}},DieselContent.ITEM.get());}
}
