package com.hbm.client.railway.track;
import com.hbm.railway.track.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
/** Actual ISBRH replacement for source item/world meshes; no ticking fake model BE. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class SourceRailClient {
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->{for(var kind:SourceRailKind.values())if(id.id().equals(SourceRailContent.id(kind.id)))return new SourceRailBakedModel(model,kind,id.getVariant().equals("inventory"),event.getTextureGetter());return model;});}
    private SourceRailClient(){}
}
