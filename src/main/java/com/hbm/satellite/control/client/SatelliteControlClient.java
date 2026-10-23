package com.hbm.satellite.control.client;
import com.hbm.satellite.control.*;import net.minecraft.client.Minecraft;import net.neoforged.api.distmarker.Dist;import net.neoforged.bus.api.SubscribeEvent;import net.neoforged.fml.common.EventBusSubscriber;import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)public final class SatelliteControlClient {
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(SatelliteControlContent.MENU.get(),SatelliteLinkerScreen::new);}@SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->SatelliteCoordinateItem.OPEN=hand->Minecraft.getInstance().setScreen(new SatelliteCoordinateScreen(hand)));}private SatelliteControlClient(){}
}
