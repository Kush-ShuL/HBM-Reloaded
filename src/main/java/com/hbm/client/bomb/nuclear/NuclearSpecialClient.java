package com.hbm.client.bomb.nuclear;

import com.hbm.bomb.nuclear.NuclearBalefireBlock;
import com.hbm.bomb.nuclear.NuclearSpecialContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class NuclearSpecialClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(NuclearSpecialContent.BALEFIRE_EXPLOSION.get(),NoopRenderer::new);event.registerBlockEntityRenderer(NuclearSpecialContent.DUD_ENTITY.get(),NuclearDudRenderer::new);}
 @SubscribeEvent public static void colors(RegisterColorHandlersEvent.Block event){event.register((state,level,pos,index)->java.awt.Color.HSBtoRGB(0,0,1F-state.getValue(NuclearBalefireBlock.META)/30F),NuclearSpecialContent.BALEFIRE.get());}
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private NuclearDudItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new NuclearDudItemRenderer();return renderer;}},NuclearSpecialContent.dudItems().toArray(net.minecraft.world.item.Item[]::new));}
 private NuclearSpecialClient(){}
}
