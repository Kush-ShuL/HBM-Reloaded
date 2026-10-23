package com.hbm.client.block.barbedwire;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
/** Native baked source HFR model serves all five variants and the already-registered ordinary wire. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class BarbedWireClient {
    private BarbedWireClient(){}
    @SubscribeEvent public static void models(ModelEvent.RegisterGeometryLoaders event){event.register(ResourceLocation.parse("hbm:source_barbed_wire"),BarbedWireGeometry.LOADER);}
}
