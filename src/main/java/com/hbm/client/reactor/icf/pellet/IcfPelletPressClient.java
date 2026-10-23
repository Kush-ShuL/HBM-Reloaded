package com.hbm.client.reactor.icf.pellet;
import com.hbm.machine.reactor.icf.pellet.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class IcfPelletPressClient {
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(IcfPelletPressContent.MENU.get(),IcfPelletPressScreen::new);}
 @SubscribeEvent public static void colors(RegisterColorHandlersEvent.Item event){event.register((stack,layer)->0xff000000|(layer==0?IcfPelletItem.color(stack):0xffffff),IcfPelletPressContent.PELLET.get());}
 private IcfPelletPressClient(){}
}
