package com.hbm.client.luminousglass;

import com.hbm.block.luminousglass.LuminousGlassContent;
import com.hbm.client.turbine.quartz.QuartzGlassModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

/** Reuse the unique original CTContext/IconCT face/20-fragment implementation; source connects only the same block. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class LuminousGlassClient {
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){
        event.getModels().replaceAll((key,model)->{
            String name=key.id().getPath();
            if(!key.id().getNamespace().equals("hbm")||!LuminousGlassContent.NAMES.contains(name)||key.getVariant().equals("inventory"))return model;
            return new QuartzGlassModel(model,
                event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.fromNamespaceAndPath("hbm","block/"+name))),
                event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.fromNamespaceAndPath("hbm","block/"+name+"_ct"))));
        });
    }
    private LuminousGlassClient(){}
}
