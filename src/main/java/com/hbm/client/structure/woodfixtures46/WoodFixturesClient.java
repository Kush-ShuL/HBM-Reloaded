package com.hbm.client.structure.woodfixtures46;

import com.hbm.block.woodfixtures46.WoodFixturesContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class WoodFixturesClient {
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event) {
        event.getModels().replaceAll((id,model)->{
            if(id.id().equals(WoodFixturesContent.BARRIER.getId()))return new WoodFixtureModel(model,true,id.getVariant().equals("inventory"),event.getTextureGetter());
            if(id.id().equals(WoodFixturesContent.STRUCTURE.getId()))return new WoodFixtureModel(model,false,false,event.getTextureGetter());
            String[] types={"roof","scaffold","ceiling"};for(int i=0;i<types.length;i++)if(id.id().getNamespace().equals("hbm")&&id.id().getPath().equals("wood_structure_"+types[i]))return new WoodFixtureModel(model,false,true,i,event.getTextureGetter());
            return model;
        });
    }
    private WoodFixturesClient() {}
}
