package com.hbm.client.reactor.rbmk.control;
import com.hbm.machine.reactor.rbmk.control.RbmkControlsContent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RbmkControlClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(RbmkControlsContent.ENTITY.get(),RbmkControlRenderer::new);}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(RbmkControlsContent.MENU.get(),RbmkControlScreen::new);}
    @SubscribeEvent public static void geometry(ModelEvent.RegisterGeometryLoaders event){event.register(ResourceLocation.parse("hbm:rbmk_piped"),RbmkPipedGeometry.LOADER);}
    private RbmkControlClient(){}
}
