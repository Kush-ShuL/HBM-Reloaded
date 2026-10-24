package com.hbm.client.jetpack;

import com.hbm.equipment.jetpack.JetpackNetwork;
import com.hbm.item.settings.client.LegacyInfoSystem;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class JetpackClient {
    public static final KeyMapping TOGGLE = new KeyMapping("hbm.key.toggleBack", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, "key.categories.hbm");
    @SubscribeEvent public static void keys(RegisterKeyMappingsEvent event) { event.register(TOGGLE); }
    @SubscribeEvent public static void layers(EntityRenderersEvent.AddLayers event) {
        for (var skin : event.getSkins()) { PlayerRenderer renderer = event.getSkin(skin); if (renderer != null) renderer.addLayer(new JetpackLayer(renderer)); }
    }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) { event.enqueueWork(() -> {
        JetpackNetwork.CLIENT_EXHAUST = JetpackParticles::exhaust;
        JetpackNetwork.CLIENT_STATE = state -> {
            var player = Minecraft.getInstance().player; if (player == null) return;
            player.getPersistentData().putBoolean("enableBackpack", state.enabled());
            if (state.inform()) LegacyInfoSystem.push(Component.translatable(state.enabled() ? "message.hbm.jetpack.on" : "message.hbm.jetpack.off")
                    .withStyle(state.enabled() ? ChatFormatting.GREEN : ChatFormatting.RED), 1000, 5);
        };
    }); }
    private JetpackClient() { }
    @EventBusSubscriber(modid = "hbm", value = Dist.CLIENT)
    public static final class Keys {
        private static net.minecraft.client.player.LocalPlayer previous;
        private static boolean jump, toggle;
        private static void reset() {
            if (previous != null) JetpackNetwork.clearKeys(previous);
            previous = null; jump = toggle = false;
        }
        @SubscribeEvent public static void logout(ClientPlayerNetworkEvent.LoggingOut event) { reset(); }
        @SubscribeEvent public static void tick(ClientTickEvent.Post event) {
            var mc = Minecraft.getInstance();
            if (mc.player == null || mc.level == null) { reset(); return; }
            if (previous != mc.player) { reset(); previous = mc.player; }
            boolean nextJump = mc.screen == null && mc.options.keyJump.isDown(), nextToggle = mc.screen == null && TOGGLE.isDown();
            if (nextJump != jump || nextToggle != toggle) {
                jump = nextJump; toggle = nextToggle; JetpackNetwork.localKeys(mc.player, jump, toggle);
                PacketDistributor.sendToServer(new JetpackNetwork.Controls(jump, toggle));
            }
        }
    }
}
