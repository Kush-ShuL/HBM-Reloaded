package com.hbm.client.reactor.rbmk.residue;

import com.hbm.machine.reactor.rbmk.residue.RbmkResidueContent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class RbmkResidueClient {
    @SubscribeEvent public static void geometry(ModelEvent.RegisterGeometryLoaders event) { event.register(ResourceLocation.parse("hbm:pribris"), PribrisGeometry.LOADER); }
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(RbmkResidueContent.DEBRIS.get(), RbmkDebrisRenderer::new); }
    private RbmkResidueClient() { }
}
