package com.hbm.client.network.rtty.telex;
import com.hbm.network.rtty.telex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RadioTelexClient {
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->RadioTelexBlock.OPEN_SCREEN=telex->Minecraft.getInstance().setScreen(new RadioTelexScreen(telex)));}@SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(RadioTelexContent.ENTITY.get(),RadioTelexRenderer::new);}@SubscribeEvent public static void items(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private RadioTelexItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new RadioTelexItemRenderer()):renderer;}},RadioTelexContent.ITEM.get());}
}
