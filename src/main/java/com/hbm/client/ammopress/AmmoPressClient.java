package com.hbm.client.ammopress;
import com.hbm.machine.ammopress.AmmoPressContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class AmmoPressClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(AmmoPressContent.ENTITY.get(),AmmoPressRenderer::new);}
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(AmmoPressContent.MENU.get(),AmmoPressScreen::new);}
 @SubscribeEvent public static void items(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private AmmoPressItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new AmmoPressItemRenderer()):renderer;}},AmmoPressContent.ITEM.get());}
}
