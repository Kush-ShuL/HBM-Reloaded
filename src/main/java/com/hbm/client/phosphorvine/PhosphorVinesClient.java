package com.hbm.client.phosphorvine;

import com.hbm.block.phosphorvine.PhosphorVinesContent;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class PhosphorVinesClient {
    @SubscribeEvent public static void additional(ModelEvent.RegisterAdditional event){for(var texture:new String[]{"vine_phosphor","vine_phosphor_ground","vine_phosphor_hang","vine_phosphor_spots","vine_phosphor_spots_hang"})event.register(ModelResourceLocation.standalone(ResourceLocation.parse("hbm:block/phosphor_vine_sprites/"+texture)));}
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.id().equals(PhosphorVinesContent.VINE.getId())&&!id.getVariant().equals("inventory")?new PhosphorVineModel(model,event.getTextureGetter()):model);}
    private PhosphorVinesClient(){}
}
