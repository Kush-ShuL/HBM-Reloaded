package com.hbm.client.assemblyfactory;

import com.hbm.machine.assemblyfactory.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class AssemblyFactoryClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{AssemblyFactoryEntity.CLIENT_TICK=AssemblyFactoryAnimation::tick;AssemblyFactoryEntity.CLIENT_REMOVED=AssemblyFactoryAnimation::removed;});}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(AssemblyFactoryContent.MENU.get(),AssemblyFactoryScreen::new);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(AssemblyFactoryContent.ENTITY.get(),AssemblyFactoryRenderer::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private AssemblyFactoryItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new AssemblyFactoryItemRenderer();return renderer;}},AssemblyFactoryContent.ITEM.get());}
}
