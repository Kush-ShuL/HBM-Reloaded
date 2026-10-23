package com.hbm.client.conveyorextraction;

import com.hbm.machine.conveyorextraction.CraneExtractionContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class CraneExtractionClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(CraneExtractionContent.EXTRACTOR_MENU.get(),CraneExtractionScreen::new);event.register(CraneExtractionContent.INSERTER_MENU.get(),CraneExtractionScreen::new);}
    private CraneExtractionClient(){}
}
