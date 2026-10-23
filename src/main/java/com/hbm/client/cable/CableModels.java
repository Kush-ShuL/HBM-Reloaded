package com.hbm.client.cable;

import com.hbm.block.cable.CableKind;
import java.util.EnumMap;
import java.util.Map;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class CableModels {
    private CableModels() {}
    @SubscribeEvent public static void bake(ModelEvent.ModifyBakingResult event) {
        Map<CableKind,CableBakedModel> blocks = new EnumMap<>(CableKind.class);
        Map<CableKind,CableBakedModel> items = new EnumMap<>(CableKind.class);
        event.getModels().replaceAll((location, model) -> {
            if (!location.id().getNamespace().equals("hbm")) return model;
            for (CableKind kind : CableKind.values()) if (location.id().getPath().equals(kind.id())) {
                var cache = location.getVariant().equals("inventory") ? items : blocks;
                return cache.computeIfAbsent(kind, ignored -> new CableBakedModel(model,kind,event.getTextureGetter(),0));
            }
            return model;
        });
    }
}
