package com.hbm.client.network.separator;
import com.hbm.network.separator.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class NetworkSeparatorClient {
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->NetworkSeparatorBlock.OPEN_SCREEN=tile->Minecraft.getInstance().setScreen(new NetworkSeparatorScreen(tile)));}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(NetworkSeparatorContent.ENTITY.get(),NetworkSeparatorRenderer::new);}
 @SubscribeEvent public static void items(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private NetworkSeparatorItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new NetworkSeparatorItemRenderer()):renderer;}},NetworkSeparatorContent.ITEM.get());}
 private NetworkSeparatorClient(){}
}
