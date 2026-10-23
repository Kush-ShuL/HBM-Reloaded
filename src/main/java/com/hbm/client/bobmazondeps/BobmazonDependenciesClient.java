package com.hbm.client.bobmazondeps;

import com.hbm.item.bobmazondeps.BobmazonDependenciesContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class BobmazonDependenciesClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(BobmazonDependenciesContent.SNOW_ENTITY.get(),SnowglobeRenderer::new);event.registerBlockEntityRenderer(BobmazonDependenciesContent.PLUSH_ENTITY.get(),PlushieRenderer::new);BobmazonDependenciesContent.CLIENT_OPEN=tile->Minecraft.getInstance().setScreen(new SnowglobeScreen(tile));}
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){for(boolean snow:new boolean[]{true,false})event.registerItem(new IClientItemExtensions(){private TrinketItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new TrinketItemRenderer(snow);return renderer;}},snow?BobmazonDependenciesContent.SNOW_ITEM.get():BobmazonDependenciesContent.PLUSH_ITEM.get());}
 private BobmazonDependenciesClient(){}
}
