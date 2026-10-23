package com.hbm.client.specialvehicle;

import com.hbm.weapon.specialvehicle.VehicleContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class VehicleClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(VehicleContent.BOXCAR_ENTITY.get(),context->new VehicleEntityRenderer<>(context,false));event.registerEntityRenderer(VehicleContent.BOAT_ENTITY.get(),context->new VehicleEntityRenderer<>(context,true));event.registerBlockEntityRenderer(VehicleContent.TILE.get(),VehicleDecoRenderer::new);}
    private VehicleClient(){}
}
