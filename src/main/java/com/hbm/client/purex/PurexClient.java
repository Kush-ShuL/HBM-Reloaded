package com.hbm.client.purex;

import com.hbm.machine.purex.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class PurexClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{PurexBlockEntity.CLIENT_TICK=PurexAnimation::tick;PurexBlockEntity.CLIENT_REMOVED=PurexAnimation::removed;net.minecraft.client.renderer.item.ItemProperties.register(PurexContent.SCHRARANIUM.get(),net.minecraft.resources.ResourceLocation.parse("hbm:lbsm"),(stack,level,entity,seed)->SchraraniumItem.nikonium()?1:0);});}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(PurexContent.MENU.get(),PurexScreen::new);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(PurexContent.ENTITY.get(),PurexRenderer::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private PurexItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new PurexItemRenderer();return renderer;}},PurexContent.ITEM.get());}
}
