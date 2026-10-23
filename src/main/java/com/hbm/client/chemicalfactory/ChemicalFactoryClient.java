package com.hbm.client.chemicalfactory;

import com.hbm.machine.chemicalfactory.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ChemicalFactoryClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{ChemicalFactoryEntity.CLIENT_TICK=ChemicalFactoryAnimation::tick;ChemicalFactoryEntity.CLIENT_REMOVED=ChemicalFactoryAnimation::removed;});}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(ChemicalFactoryContent.MENU.get(),ChemicalFactoryScreen::new);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(ChemicalFactoryContent.ENTITY.get(),ChemicalFactoryRenderer::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private ChemicalFactoryItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new ChemicalFactoryItemRenderer();return renderer;}},ChemicalFactoryContent.ITEM.get());}
}
