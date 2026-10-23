package com.hbm.client.duck;

import com.hbm.entity.mob.duck.DuckCallPayloads;
import com.hbm.item.settings.client.LegacyInfoSystem;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class DuckCallClient {
    private static final KeyMapping DUCK = new KeyMapping("hbm.key.duck", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_O, "key.categories.hbm");
    // This is the original client-process latch, intentionally not reset on key-up, reconnect or respawn.
    private static boolean ducked;

    @SubscribeEvent public static void keys(RegisterKeyMappingsEvent event) { event.register(DUCK); }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> DuckCallPayloads.CLIENT_REMINDER = () -> LegacyInfoSystem.push(
                Component.translatable("message.hbm.duck.pressKey", DUCK.getTranslatedKeyMessage()), 30_000, 0));
    }

    @EventBusSubscriber(modid = "hbm", value = Dist.CLIENT)
    public static final class Input {
        @SubscribeEvent public static void render(RenderGuiEvent.Pre event) {
            var client = Minecraft.getInstance();
            if (ducked || client.player == null || client.level == null || client.screen != null || !DUCK.isDown()) return;
            ducked = true;
            PacketDistributor.sendToServer(new DuckCallPayloads.Request());
        }
        private Input() {}
    }
    private DuckCallClient() {}
}
