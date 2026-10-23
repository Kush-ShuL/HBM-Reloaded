package com.hbm.client.glitch;

import com.hbm.LegacyStartup;
import com.hbm.item.glitch.GlitchContent;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/** Source startup-wide chosen glitch icon; use native property routing, not a new renderer or random per-frame fate. */
@EventBusSubscriber(modid="hbm", value=Dist.CLIENT, bus=EventBusSubscriber.Bus.MOD)
public final class GlitchClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(GlitchContent.GLITCH.get(), ResourceLocation.parse("hbm:glitch_fate"),
            (stack, level, entity, seed) -> LegacyStartup.polaroidId / 18F));
    }
    private GlitchClient() { }
}
