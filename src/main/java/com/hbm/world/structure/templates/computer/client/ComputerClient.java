package com.hbm.world.structure.templates.computer.client;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ComputerClient {
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((key,model)->{if(!key.id().getNamespace().equals("hbm")||!key.id().getPath().equals("deco_computer"))return model;String variant=key.getVariant();if(variant.equals("inventory"))return new ComputerModel(model,true,0,event.getTextureGetter());if(variant.startsWith("legacy_meta="))return new ComputerModel(model,false,Integer.parseInt(variant.substring(12)),event.getTextureGetter());return model;});}
    private ComputerClient(){}
}
