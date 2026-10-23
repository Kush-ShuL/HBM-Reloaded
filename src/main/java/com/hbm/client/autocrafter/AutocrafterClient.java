package com.hbm.client.autocrafter;
import com.hbm.machine.autocrafter.AutocrafterContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class AutocrafterClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(AutocrafterContent.MENU.get(),AutocrafterScreen::new);}
    private AutocrafterClient(){}
}
