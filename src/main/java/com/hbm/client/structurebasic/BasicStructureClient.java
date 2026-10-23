package com.hbm.client.structurebasic;
import com.hbm.block.structurebasic.*;
import com.hbm.hazard.HbmHazards;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class BasicStructureClient {
 @SubscribeEvent public static void sprites(ModelEvent.RegisterAdditional event){for(String part:new String[]{"mid","bottom"})event.register(ModelResourceLocation.standalone(ResourceLocation.parse("hbm:block/reeds_"+part)));}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(BasicStructureContent.REEDS_ENTITY.get(),ReedsRenderer::new);}
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->ItemProperties.register(BasicStructureContent.VINYL_ITEM.get(),ResourceLocation.parse("hbm:legacy_metadata"),(stack,level,entity,seed)->Math.abs(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)%2)));}
}
