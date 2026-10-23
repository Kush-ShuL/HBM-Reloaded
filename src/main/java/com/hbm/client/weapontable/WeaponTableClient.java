package com.hbm.client.weapontable;
import com.hbm.machine.weapontable.WeaponTableContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class WeaponTableClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(WeaponTableContent.MENU.get(),WeaponTableScreen::new);}
}
