package com.hbm.client.reactor.rbmk.autoloader;
import com.hbm.machine.reactor.rbmk.autoloader.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RbmkAutoloaderClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(RbmkAutoloaderContent.ENTITY.get(),RbmkAutoloaderRenderer::new);}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(RbmkAutoloaderContent.MENU.get(),RbmkAutoloaderScreen::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private RbmkAutoloaderItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new RbmkAutoloaderItemRenderer()):renderer;}},RbmkAutoloaderContent.ITEM.get());}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{RbmkAutoloaderEntity.CLIENT_TICK=RbmkAutoloaderAnimation::tick;RbmkAutoloaderEntity.CLIENT_REMOVED=RbmkAutoloaderAnimation::removed;});}
    private RbmkAutoloaderClient(){}
}
