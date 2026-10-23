package com.hbm.client.weapon.energy;
import com.hbm.weapon.sedna.gun.energy.bag.AmmoBagContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class AmmoBagClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(AmmoBagContent.AMMO_BAG_MENU.get(),AmmoBagScreen::new);}
}
