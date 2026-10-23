package com.hbm.world.structure.templates.client;

import com.hbm.world.structure.templates.materials.TemplateMaterials;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class TemplateMaterialClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{for(var holder:TemplateMaterials.enums()){var item=holder.get();ItemProperties.register(item,ResourceLocation.parse("hbm:legacy_metadata"),(stack,level,owner,seed)->item.ordinal(stack));}ItemBlockRenderTypes.setRenderLayer(TemplateMaterials.DEAD_PLANT.get(),RenderType.cutout());ItemBlockRenderTypes.setRenderLayer(TemplateMaterials.GLASS.get(),RenderType.cutout());});}
}
