package com.hbm.client.generator.gasturbine;

import com.hbm.machine.generator.gasturbine.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class GasTurbineClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{GasTurbineBlockEntity.CLIENT_TICK=GasTurbineSound::tick;GasTurbineBlockEntity.CLIENT_REMOVED=GasTurbineSound::removed;});}
    @SubscribeEvent public static void renderer(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(GasTurbineContent.ENTITY.get(),GasTurbineRenderer::new);}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(GasTurbineContent.MENU.get(),GasTurbineScreen::new);}
    @SubscribeEvent public static void items(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private GasTurbineItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new GasTurbineItemRenderer()):renderer;}},GasTurbineContent.ITEM.get());}
    private GasTurbineClient(){}
}
