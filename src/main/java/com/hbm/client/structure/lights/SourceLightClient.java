package com.hbm.client.structure.lights;
import com.hbm.block.light.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
/** Native mesh rendering for the real six source lamp blocks, not a static glowing substitute. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class SourceLightClient{
 @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->{for(var kind:SpotlightKind.values())for(boolean on:new boolean[]{true,false})if(id.id().equals((on?SourceLightContent.ON:SourceLightContent.OFF).get(kind).getId()))return new SourceLightModel(model,kind,on,id.getVariant().equals("inventory"),event.getTextureGetter());return model;});}
}
