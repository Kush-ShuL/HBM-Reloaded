package com.hbm.client.digamma;

import com.hbm.block.digamma.AshVisuals;
import java.util.HashMap;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class DigammaBlocksClient {
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->AshVisuals.ACCUMULATE=AshClientState::accumulate);}
 @SubscribeEvent public static void bake(ModelEvent.ModifyBakingResult event){
  var cache=new HashMap<String,DigammaMatterModel>();
  event.getModels().replaceAll((key,model)->!key.id().getNamespace().equals("hbm")||!key.id().getPath().equals("digamma_matter")||key.getVariant().equals("inventory")?model:cache.computeIfAbsent("matter",ignored->new DigammaMatterModel(model,event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/digamma_matter"))))));
 }
 private DigammaBlocksClient(){}
}
