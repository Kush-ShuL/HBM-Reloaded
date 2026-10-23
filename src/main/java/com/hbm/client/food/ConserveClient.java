package com.hbm.client.food;

import com.hbm.block.crate.ConserveContent;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ConserveClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->ItemProperties.register(ConserveContent.CONSERVE.get(),ResourceLocation.parse("hbm:legacy_metadata"),(stack,level,entity,seed)->ConserveContent.CONSERVE.get().ordinal(stack)));}
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.id().equals(ResourceLocation.parse("hbm:crate_can"))?new ConserveCrateModel(model,id.getVariant().equals("inventory"),event.getTextureGetter()):model);}
    private ConserveClient(){}
}
