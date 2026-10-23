package com.hbm.client.reactor.iter.torus;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.reactor.iter.torus.*;
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
public final class TorusClient {
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{ItemProperties.register(TorusContent.DRIVE.get(),ResourceLocation.parse("hbm:legacy_metadata"),(stack,level,entity,seed)->TorusContent.DRIVE.get().ordinal(stack));TorusEntity.CLIENT_TICK=TorusSound::tick;TorusEntity.CLIENT_REMOVED=TorusSound::removed;ItemProperties.register(TorusContent.COMPONENT_ITEM.get(),ResourceLocation.parse("hbm:legacy_metadata"),(stack,level,entity,seed)->stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0));});}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(TorusContent.ENTITY.get(),TorusRenderer::new);event.registerBlockEntityRenderer(TorusContent.STRUCT_ENTITY.get(),TorusStructRenderer::new);}
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(TorusContent.MENU.get(),TorusScreen::new);}
 @SubscribeEvent public static void items(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private TorusItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new TorusItemRenderer()):renderer;}},TorusContent.ITEM.get());}
 private TorusClient(){}
}
