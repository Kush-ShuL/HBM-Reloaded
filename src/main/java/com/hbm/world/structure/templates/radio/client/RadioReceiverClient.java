package com.hbm.world.structure.templates.radio.client;
import com.hbm.world.structure.templates.radio.*;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RadioReceiverClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->RadioReceiverClientBridge.open=tile->Minecraft.getInstance().setScreen(new RadioReceiverScreen(tile)));}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(RadioReceiverContent.ENTITY.get(),RadioReceiverRenderer::new);}
    private RadioReceiverClient(){}
}
