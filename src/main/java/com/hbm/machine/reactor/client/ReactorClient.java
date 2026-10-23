package com.hbm.machine.reactor.client;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.reactor.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ReactorClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(ReactorContent.RESEARCH_MENU.get(),ResearchReactorScreen::new);event.register(ReactorContent.BREEDING_MENU.get(),BreedingReactorScreen::new);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(ReactorContent.RESEARCH_ENTITY.get(),ResearchReactorRenderer::new);event.registerBlockEntityRenderer(ReactorContent.BREEDING_ENTITY.get(),BreedingReactorRenderer::new);event.registerEntityRenderer(ReactorContent.WASTE_ENTITY.get(),ItemEntityRenderer::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private ReactorItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new ReactorItemRenderer();return renderer;}},ReactorContent.RESEARCH_ITEM.get(),ReactorContent.BREEDING_ITEM.get());}
    @SubscribeEvent public static void colors(RegisterColorHandlersEvent.Item event){event.register((stack,layer)->stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)>0?0xffffbfa5:0xffffffff,ReactorContent.wasteItems().toArray(net.minecraft.world.level.ItemLike[]::new));}
}
