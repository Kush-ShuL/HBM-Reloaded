package com.hbm.client.railway.cart;
import com.hbm.railway.cart.*;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",bus=EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class SourceCartClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(SourceCartContent.EMPTY.get(),SourceCartRenderer::new);
        event.registerEntityRenderer(SourceCartContent.DESTROYER.get(),SourceCartRenderer::new);
        event.registerEntityRenderer(SourceCartContent.POWDER.get(),SourceCartRenderer::new);
        event.registerEntityRenderer(SourceCartContent.SEMTEX.get(),SourceCartRenderer::new);
        event.registerEntityRenderer(SourceCartContent.CRATE.get(),context->new MinecartRenderer<>(context,ModelLayers.MINECART));
    }
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(SourceCartContent.CRATE_MENU.get(),CrateCartScreen::new);event.register(SourceCartContent.DESTROYER_MENU.get(),DestroyerCartScreen::new);}
    private SourceCartClient(){}
}
