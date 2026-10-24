package com.hbm.client.structure.toaster;

import com.hbm.block.toaster.ToasterContent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class ToasterClient {
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event) {
        event.getModels().replaceAll((id, model) -> {
            String variant = id.getVariant();
            if (variant.equals("inventory")) {
                for (var item : ToasterContent.items()) if (id.id().equals(BuiltInRegistries.ITEM.getKey(item)))
                    return ToasterModel.inventory(model, item.kind().type(), event.getTextureGetter());
                return model;
            }
            if (!id.id().equals(ToasterContent.BLOCK.getId())) return model;
            if (variant.startsWith("legacy_meta=")) return new ToasterModel(model, false, Integer.parseInt(variant.substring(12)), event.getTextureGetter());
            return model;
        });
    }
    private ToasterClient() {}
}
