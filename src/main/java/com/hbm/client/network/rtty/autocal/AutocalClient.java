package com.hbm.client.network.rtty.autocal;
import com.hbm.network.rtty.autocal.*;
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
public final class AutocalClient {
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->AutocalBlock.OPEN_SCREEN=telex->Minecraft.getInstance().setScreen(new AutocalScreen(telex)));}@SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(AutocalContent.ENTITY.get(),AutocalRenderer::new);}@SubscribeEvent public static void items(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private AutocalItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new AutocalItemRenderer()):renderer;}},AutocalContent.ITEM.get());}
}
