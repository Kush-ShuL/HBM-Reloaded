package com.hbm.client.scaffold;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class ScaffoldClient {
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event) {
        event.getModels().replaceAll((id, model) -> {
            if (!id.id().getNamespace().equals("hbm") || !id.id().getPath().equals("steel_scaffold")) return model;
            if (id.getVariant().equals("inventory")) return ScaffoldBakedModel.inventory(model, event.getTextureGetter());
            String variant = id.getVariant();
            for (String property : variant.split(",")) {
                if (property.startsWith("legacy_meta=")) return ScaffoldBakedModel.world(model, Integer.parseInt(property.substring("legacy_meta=".length())), event.getTextureGetter());
            }
            return model;
        });
    }
}
