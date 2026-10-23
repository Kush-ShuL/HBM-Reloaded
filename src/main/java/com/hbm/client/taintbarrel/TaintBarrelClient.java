package com.hbm.client.taintbarrel;

import com.hbm.bomb.taintbarrel.TaintBarrelContent;
import com.hbm.client.barrel.BarrelBakedModel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

/** Reuse the actual BlockFluidBarrel renderer's sole mesh/UV/shading/item transforms, replacing only its source icon. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class TaintBarrelClient {
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.id().equals(TaintBarrelContent.BARREL.getId())?new BarrelBakedModel(model,id.getVariant().equals("inventory"),event.getTextureGetter(),ResourceLocation.parse("hbm:block/barrel_taint")):model);}
    private TaintBarrelClient(){}
}
