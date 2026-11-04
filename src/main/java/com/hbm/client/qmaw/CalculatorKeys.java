package com.hbm.client.qmaw;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class CalculatorKeys {
    public static final KeyMapping KEY = new KeyMapping("hbm.key.calculator", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N, "hbm.key");

    private CalculatorKeys() { }

    @SubscribeEvent
    public static void keys(RegisterKeyMappingsEvent event) { event.register(KEY); }

    private static void open() {
        var client = Minecraft.getInstance();
        if (client.player == null) return;
        client.setScreen(new CalculatorScreen());
    }

    @EventBusSubscriber(modid = "hbm", value = Dist.CLIENT)
    public static final class Events {
        @SubscribeEvent
        public static void tick(ClientTickEvent.Post event) {
            var client = Minecraft.getInstance();
            // Open after GLFW has dispatched the opening key's character event.
            while (KEY.consumeClick()) {
                if (client.screen == null) open();
            }
        }
    }
}
