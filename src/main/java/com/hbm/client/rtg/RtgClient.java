package com.hbm.client.rtg;

import com.hbm.machine.rtg.RtgContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RtgClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(RtgContent.MENU.get(),RtgScreen::new);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(RtgContent.ENTITY.get(),RtgRenderer::new);}
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.id().equals(RtgContent.BLOCK.getId())&&id.getVariant().equals("inventory")?new RtgItemModel(model,event.getTextureGetter()):model);}
    private RtgClient(){}
}
