package com.hbm.client.chemicalplant;

import com.hbm.machine.chemicalplant.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ChemicalPlantClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{ChemicalPlantBlockEntity.CLIENT_TICK=ChemicalPlantAnimation::tick;ChemicalPlantBlockEntity.CLIENT_REMOVED=ChemicalPlantAnimation::removed;});}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(ChemicalPlantContent.MENU.get(),ChemicalPlantScreen::new);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(ChemicalPlantContent.ENTITY.get(),ChemicalPlantRenderer::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private ChemicalPlantItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new ChemicalPlantItemRenderer();return renderer;}},ChemicalPlantContent.ITEM.get());}
}
