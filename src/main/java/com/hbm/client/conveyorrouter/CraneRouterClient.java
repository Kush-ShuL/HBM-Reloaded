package com.hbm.client.conveyorrouter;

import com.hbm.machine.conveyorrouter.CraneRouterContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class CraneRouterClient {
    private static final int[] COLORS={0xff0000,0xff8000,0xffff00,0x00ff00,0x0080ff,0x8000ff};
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(CraneRouterContent.MENU.get(),CraneRouterScreen::new);}
    @SubscribeEvent public static void colors(RegisterColorHandlersEvent.Block event){event.register((state,level,pos,tint)->tint>=0&&tint<6?COLORS[tint]:0xffffff,CraneRouterContent.BLOCK.get());}
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((location,model)->location.id().toString().equals("hbm:crane_router")?new CraneRouterModel(model,event.getTextureGetter()):model);}
    private CraneRouterClient(){}
}
