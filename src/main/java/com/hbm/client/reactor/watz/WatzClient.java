package com.hbm.client.reactor.watz;

import com.hbm.hazard.HbmHazards;
import com.hbm.machine.reactor.watz.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class WatzClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->ItemProperties.register(WatzContent.CASING.get().asItem(),ResourceLocation.parse("hbm:legacy_metadata"),(stack,level,entity,seed)->stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)));}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(WatzContent.ENTITY.get(),WatzRenderer::new);event.registerBlockEntityRenderer(WatzContent.PUMP_ENTITY.get(),WatzPumpRenderer::new);event.registerBlockEntityRenderer(WatzContent.STRUCT_ENTITY.get(),WatzStructRenderer::new);}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(WatzContent.MENU.get(),WatzScreen::new);}
    @SubscribeEvent public static void items(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private WatzItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new WatzItemRenderer()):renderer;}},WatzContent.REACTOR.get().asItem(),WatzContent.PUMP.get().asItem());}
    private WatzClient(){}
}
