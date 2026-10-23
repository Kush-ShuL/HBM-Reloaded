package com.hbm.client.structure.materials45;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.refinery.deco.DecoSteelModel;
import com.hbm.world.structure.dependencies.materials45.*;
import java.util.*;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
/** Reuse shared exact source CTContext/IconCT geometry; same-block connections intentionally ignore source color metadata. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class StructuralMaterials45Client {
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->ItemProperties.register(StructuralMaterials45Content.PLATEMETAL_ITEM.get(),StructuralMaterials45Content.id("metadata"),(stack,world,entity,seed)->PlatemetalBlock.rectify(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0))));}
 @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){var cache=new HashMap<String,BakedModel>();event.getModels().replaceAll((key,model)->{
  String name=key.id().getPath();if(!key.id().getNamespace().equals("hbm")||key.getVariant().equals("inventory")||!List.of("platemetal","glass_ash").contains(name))return model;
  var props=key.getVariant();int metadata=0;for(String pair:props.split(","))if(pair.startsWith("legacy_meta="))metadata=Integer.parseInt(pair.substring(12));
  String texture=name.equals("platemetal")?"platemetal."+PlatemetalBlock.NAMES.get(metadata%15):"glass_ash";
  return cache.computeIfAbsent(texture,ignored->DecoSteelModel.wrap(model,event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS,StructuralMaterials45Content.id("block/"+texture))),event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS,StructuralMaterials45Content.id("block/"+texture+"_ct"))),(state,other)->other.is(state.getBlock())));
 });}
 private StructuralMaterials45Client(){}
}
