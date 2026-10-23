package com.hbm.client.reactor.fuelpool;
import com.hbm.machine.reactor.fuelpool.FuelPoolContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class FuelPoolClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(FuelPoolContent.MENU.get(),FuelPoolScreen::new);}
    private FuelPoolClient(){}
}
