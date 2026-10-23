package com.hbm.client.massstorage;

import com.hbm.machine.massstorage.*;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class MassStorageClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(MassStorageContent.MENU.get(),MassStorageScreen::new);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(MassStorageContent.ENTITY.get(),MassStorageRenderer::new);}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->ItemProperties.register(MassStorageContent.ITEM.get(),ResourceLocation.parse("hbm:legacy_metadata"),(stack,level,entity,seed)->MassStorageItem.tier(stack)));}
    private MassStorageClient(){}
}
