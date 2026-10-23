package com.hbm.client.pedestal;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class PedestalMaterialClient {
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->{
        if(!id.id().getNamespace().equals("hbm"))return model;boolean inventory=id.getVariant().equals("inventory");
        if(id.id().getPath().equals("barbed_wire"))return new PedestalMaterialModel(model,true,inventory,event.getTextureGetter());
        if(id.id().getPath().equals("dungeon_chain")&&!inventory)return new PedestalMaterialModel(model,false,false,event.getTextureGetter());return model;
    });}
}
