package com.hbm.client.polarizedglass;

import com.hbm.block.polarizedglass.PolarizedGlassContent;
import com.hbm.client.turbine.quartz.QuartzGlassModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

/** Sole existing source CTContext/IconCT implementation; parent native material retains source pass0 cutout. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class PolarizedGlassClient {
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.id().equals(PolarizedGlassContent.GLASS.getId())&&!id.getVariant().equals("inventory")?new QuartzGlassModel(model,event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/glass_polarized"))),event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/glass_polarized_ct")))):model);}
    private PolarizedGlassClient(){}
}
