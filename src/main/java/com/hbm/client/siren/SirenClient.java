package com.hbm.client.siren;

import com.hbm.machine.siren.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class SirenClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(SirenContent.MENU.get(),SirenScreen::new);}
    @SubscribeEvent public static void colors(RegisterColorHandlersEvent.Item event){event.register((stack,layer)->{if(layer==0)return 0xffffffff;int color=CassetteItem.track(stack).color;return 0xff000000|(color<0?0xffffff:color);},SirenContent.cassetteItems().toArray(net.minecraft.world.item.Item[]::new));}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->SirenPacket.CLIENT=SirenSound::packet);}
    private SirenClient(){}
}
