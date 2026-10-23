package com.hbm.client.structure.sandbags;

import com.hbm.block.sandbags.SandbagsContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class SandbagsClient {
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event) {
        event.getModels().replaceAll((id, model) -> id.id().equals(SandbagsContent.BLOCK.getId()) ? new SandbagsModel(model, id.getVariant().equals("inventory"), event.getTextureGetter()) : model);
    }
    private SandbagsClient() {}
}
