package com.hbm.client.reactor.icf;
import com.hbm.client.turbine.quartz.QuartzGlassModel;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.reactor.icf.*;
import java.util.HashMap;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class IcfClient{
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(IcfReactorContent.REACTOR_ENTITY.get(),IcfRenderer::new);event.registerBlockEntityRenderer(IcfReactorContent.STRUCT_ENTITY.get(),IcfStructRenderer::new);event.registerBlockEntityRenderer(IcfReactorContent.LASER_ENTITY.get(),IcfLaserRenderer::new);}@SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(IcfReactorContent.MENU.get(),IcfScreen::new);}@SubscribeEvent public static void items(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private IcfItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new IcfItemRenderer()):renderer;}},IcfReactorContent.REACTOR_ITEM.get());}
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{for(var block:java.util.List.of(IcfReactorContent.COMPONENT.get(),IcfReactorContent.LASER_COMPONENT.get()))ItemProperties.register(block.asItem(),ResourceLocation.parse("hbm:legacy_metadata"),(stack,level,entity,seed)->block.subtype(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)));});}
 @SubscribeEvent public static void ct(ModelEvent.ModifyBakingResult event){var cache=new HashMap<Boolean,QuartzGlassModel>();event.getModels().replaceAll((key,model)->{if(!key.id().equals(ResourceLocation.parse("hbm:icf_block"))||key.getVariant().equals("inventory"))return model;boolean port=key.getVariant().contains("port=true");String name=port?"icf_block_port":"icf_block";return cache.computeIfAbsent(port,ignored->new QuartzGlassModel(model,event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/"+name))),event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/"+name+"_ct"))),neighbor->neighbor.is(IcfReactorContent.FORMED.get())||neighbor.is(IcfReactorContent.LASER.get())));});}private IcfClient(){}
}
