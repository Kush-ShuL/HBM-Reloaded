package com.hbm.client.barrel;
import com.hbm.machine.barrel.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class BarrelClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(BarrelContent.MENU.get(),BarrelScreen::new);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(BarrelContent.ENTITY.get(),BarrelWorldRenderer::new);}
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->{if(!id.id().getNamespace().equals("hbm"))return model;for(var variant:BarrelContent.Variant.values())if(id.id().getPath().equals(variant.id()))return new BarrelBakedModel(model,id.getVariant().equals("inventory"),event.getTextureGetter(),variant);return model;});}
}
