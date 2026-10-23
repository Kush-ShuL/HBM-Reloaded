package com.hbm.client.fluidcontrols;
import com.hbm.network.fluidcontrols.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class FluidControlsClient {
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->FlowPumpBlock.OPEN_SCREEN=pump->Minecraft.getInstance().setScreen(new PumpScreen(pump)));}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(FluidControlsContent.OLD_ENTITY.get(),PumpRenderer::new);event.registerBlockEntityRenderer(FluidControlsContent.FLOW_ENTITY.get(),PumpRenderer::new);}
 @SubscribeEvent public static void items(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private PumpItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new PumpItemRenderer()):renderer;}},FluidControlsContent.OLD_ITEM.get(),FluidControlsContent.FLOW_ITEM.get());}
 private FluidControlsClient(){}
}
