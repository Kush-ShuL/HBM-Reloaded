package com.hbm.client.structurewand;

import com.hbm.item.structurewand.StructureWandContent;
import com.hbm.item.structurewand.StructureWandPackets;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class StructureWandClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->StructureWandPackets.CLIENT_OPEN=packet->Minecraft.getInstance().setScreen(new StructureAuthoringScreen(packet)));}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(StructureWandContent.ENTITY.get(),StructureAuthoringRenderer::new);}
    private StructureWandClient(){}
}
