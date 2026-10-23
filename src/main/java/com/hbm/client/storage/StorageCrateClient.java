package com.hbm.client.storage;

import com.hbm.machine.storage.StorageCrateContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class StorageCrateClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(StorageCrateContent.MENU.get(),StorageCrateScreen::new);}
    private StorageCrateClient(){}
}
