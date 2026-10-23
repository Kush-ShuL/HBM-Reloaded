package com.hbm.client.toolbox;

import com.hbm.item.toolbox.ToolboxContent;
import com.hbm.machine.storage.CrateStackData;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid="hbm", value=Dist.CLIENT, bus=EventBusSubscriber.Bus.MOD)
public final class ToolboxClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event) { event.register(ToolboxContent.MENU.get(), ToolboxScreen::new); }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(ToolboxContent.TOOLBOX.get(), ResourceLocation.parse("hbm:toolbox_open"),
                (stack, level, owner, seed) -> CrateStackData.data(stack).getBoolean("isOpen") ? 1 : 0));
    }
}
