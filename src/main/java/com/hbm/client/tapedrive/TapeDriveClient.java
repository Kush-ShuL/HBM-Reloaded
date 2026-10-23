package com.hbm.client.tapedrive;
import com.hbm.machine.tapedrive.TapeDriveContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class TapeDriveClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(TapeDriveContent.ENTITY.get(),TapeDriveRenderer::new);}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(TapeDriveContent.MENU.get(),TapeDriveScreen::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private TapeDriveItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new TapeDriveItemRenderer()):renderer;}},TapeDriveContent.ITEM.get());}
    private TapeDriveClient(){}
}
