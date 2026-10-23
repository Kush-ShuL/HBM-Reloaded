package com.hbm.client.capacitors;
import com.hbm.machine.capacitors.CapacitorContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class CapacitorClient {
 @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->{for(var entry:CapacitorContent.CAPACITORS.entrySet())if(id.id().equals(entry.getValue().getId())){boolean inventory=id.getVariant().equals("inventory");if(inventory||id.getVariant().startsWith("legacy_meta="))return new CapacitorModel(model,entry.getKey(),inventory?0:Integer.parseInt(id.getVariant().substring(12)),inventory,event.getTextureGetter());}return model;});}
}
