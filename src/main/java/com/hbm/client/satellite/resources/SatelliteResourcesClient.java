package com.hbm.client.satellite.resources;
import com.hbm.client.armor.plainfsb.LegacyFsbBipedClient;
import com.hbm.satellite.resources.*;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class SatelliteResourcesClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(SatelliteResources113.AGENT.get(),FbiRenderer::new);event.registerEntityRenderer(SatelliteResources113.DRONE.get(),FbiDroneRenderer::new);event.registerEntityRenderer(SatelliteResources113.TOM.get(),TomRenderer::new);event.registerEntityRenderer(SatelliteResources113.CLOUD.get(),TomCloudRenderer::new);event.registerEntityRenderer(SatelliteResources113.BLAST.get(),NoopRenderer::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){LegacyFsbBipedClient.registerPlainBiped(event,"hbm:paa_plate","hbm:paa_legs","hbm:paa_boots");}
    private SatelliteResourcesClient(){}
}
