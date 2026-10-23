package com.hbm.client.sourcestones;

import com.hbm.block.sourcestones.SourceStonesContent;
import java.util.Objects;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class SourceStonesClient {
    private static ModelResourceLocation model(int meta,boolean joined){return ModelResourceLocation.standalone(ResourceLocation.parse("hbm:block/stone_biome_"+(meta==0?"desert":"woodland")+(joined?"_joined":"_exposed")));}
    @SubscribeEvent public static void additional(ModelEvent.RegisterAdditional event){for(int meta=0;meta<2;meta++){event.register(model(meta,true));event.register(model(meta,false));}}
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){var joined=new BakedModel[2];var exposed=new BakedModel[2];for(int meta=0;meta<2;meta++){joined[meta]=Objects.requireNonNull(event.getModels().get(model(meta,true)),"Source biome-stone joined model");exposed[meta]=Objects.requireNonNull(event.getModels().get(model(meta,false)),"Source biome-stone exposed model");}event.getModels().replaceAll((id,original)->id.id().equals(SourceStonesContent.BIOME.getId())&&!id.getVariant().equals("inventory")?new BiomeStoneModel(original,joined,exposed):original);}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->ItemProperties.register(SourceStonesContent.BIOME_ITEM.get(),ResourceLocation.parse("hbm:legacy_metadata"),(stack,level,entity,seed)->SourceStonesContent.BIOME_ITEM.get().ordinal(stack)));}
    private SourceStonesClient(){}
}
