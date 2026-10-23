package com.hbm.client.meteordeco;
import com.hbm.block.meteordeco.MeteorDecorationContent;
import com.hbm.hazard.HbmHazards;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class MeteorDecorationClient {
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->ItemProperties.register(MeteorDecorationContent.FENCE_ITEM.get(),ResourceLocation.fromNamespaceAndPath("hbm","legacy_metadata"),(stack,level,entity,seed)->stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)));}
 @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->{if(!id.id().getNamespace().equals("hbm"))return model;String name=id.id().getPath();boolean inventory=id.getVariant().equals("inventory");if(name.equals("fence_metal"))return inventory?model:new MeteorBakedModel(model,false,false,event.getTextureGetter());if(name.equals("steel_wall")||name.equals("steel_corner"))return new MeteorBakedModel(model,inventory,name.equals("steel_corner"),event.getTextureGetter());return model;});}
}
