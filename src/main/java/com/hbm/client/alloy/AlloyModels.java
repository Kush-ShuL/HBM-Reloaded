package com.hbm.client.alloy;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class AlloyModels {
    private AlloyModels() {}
    @SubscribeEvent public static void bake(ModelEvent.ModifyBakingResult event) { event.getModels().replaceAll((id,model)->id.id().getNamespace().equals("hbm")&&id.id().getPath().equals("machine_difurnace_extension")?new AlloyExtensionModel(model,id.getVariant().equals("inventory"),event.getTextureGetter()):model); }
}
