package com.hbm.world.structure.templates.client;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class TemplateSpikesClient {
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((key,model)->key.id().getNamespace().equals("hbm")&&key.id().getPath().equals("spikes")?new TemplateSpikesModel(model,key.getVariant().equals("inventory"),event.getTextureGetter()):model);}
    private TemplateSpikesClient(){}
}
