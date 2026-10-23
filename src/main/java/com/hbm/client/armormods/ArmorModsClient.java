package com.hbm.client.armormods;

import com.hbm.armor.mods.MagnetNetwork;
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
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class ArmorModsClient {
    public static final KeyMapping MAGNET = new KeyMapping("hbm.key.toggleMagnet", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Z, "key.categories.hbm");
    @SubscribeEvent public static void keys(RegisterKeyMappingsEvent event) { event.register(MAGNET); }
    @SubscribeEvent public static void layers(EntityRenderersEvent.AddLayers event) {
        for (var skin : event.getSkins()) { PlayerRenderer renderer = event.getSkin(skin); if (renderer != null) renderer.addLayer(new TeslaBackLayer(renderer)); }
    }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) { event.enqueueWork(() -> {
        MagnetNetwork.CLIENT_STATE = state -> {
            var player = Minecraft.getInstance().player; if (player == null) return;
            player.getPersistentData().putBoolean("enableMagnet", state.enabled());
            if (state.inform()) LegacyInfoSystem.push(Component.translatable(state.enabled() ? "message.hbm.magnet.on" : "message.hbm.magnet.off")
                    .withStyle(state.enabled() ? ChatFormatting.GREEN : ChatFormatting.RED), 1000, 6);
        };
    }); }
    @EventBusSubscriber(modid = "hbm", value = Dist.CLIENT)
    public static final class Keys {
        private static net.minecraft.client.player.LocalPlayer previous;
        private static boolean pressed;
        @SubscribeEvent public static void tick(ClientTickEvent.Post event) {
            var mc = Minecraft.getInstance();
            if (mc.player == null || mc.level == null) { previous = null; pressed = false; return; }
            if (previous != mc.player) { previous = mc.player; pressed = false; }
            boolean next = mc.screen == null && MAGNET.isDown();
            if (next != pressed) { pressed = next; PacketDistributor.sendToServer(new MagnetNetwork.Controls(pressed)); }
        }
    }
    private ArmorModsClient() { }
}
