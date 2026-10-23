package com.hbm.client.reactor.rbmk.glass;

import com.hbm.client.turbine.quartz.QuartzGlassModel;
import java.util.Map;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

/** Reuses the single actual source CTContext/IconCT kernel already used by quartz glass. */
@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class RbmkGlassClient {
    @SubscribeEvent public static void bake(ModelEvent.ModifyBakingResult event) {
        Map<String, QuartzGlassModel> models = new java.util.HashMap<>();
        event.getModels().replaceAll((key, model) -> {
            String name = key.id().getPath();
            if (!key.id().getNamespace().equals("hbm") || !(name.equals("glass_boron") || name.equals("glass_lead")) || key.getVariant().equals("inventory")) return model;
            return models.computeIfAbsent(name, ignored -> new QuartzGlassModel(model,
                    event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.parse("hbm:block/" + name))),
                    event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.parse("hbm:block/" + name + "_ct")))));
        });
    }
    private RbmkGlassClient() { }
}
