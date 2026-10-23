package com.hbm.client.conveyorpartitioner;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ConveyorPartitionerClient {
    @SubscribeEvent public static void bake(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((location,model)->{if(!location.id().toString().equals("hbm:crane_partitioner"))return model;boolean inventory=location.getVariant().equals("inventory");int metadata=inventory?0:Integer.parseInt(location.getVariant().substring("legacy_meta=".length()));return new ConveyorPartitionerModel(model,metadata,inventory,event.getTextureGetter());});}
    private ConveyorPartitionerClient(){}
}
