package com.hbm.client.diagnostic;

import com.hbm.item.diagnostic.ColtanCompassItem;
import com.hbm.item.diagnostic.DiagnosticToolsContent;
import com.hbm.item.settings.client.LegacyInfoSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

/** Native item predicates select the original32 unmodified sprite frames; one original global spring. */
@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class DiagnosticToolsClient {
    private static double currentAngle, angleDelta;
    private static int frame;

    @SubscribeEvent public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ColtanCompassItem.CLIENT_DISTANCE = message -> LegacyInfoSystem.push(Component.literal(message), 2);
            ItemProperties.register(DiagnosticToolsContent.COLTAN.get(), ResourceLocation.parse("hbm:coltan_frame"),
                    (stack, world, entity, seed) -> frame / 32F);
        });
    }

    @SubscribeEvent public static void reload(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((ResourceManagerReloadListener) manager -> {
            currentAngle = 0;
            angleDelta = 0;
            frame = 0;
        });
    }

    private static void tickCompass() {
        var client = Minecraft.getInstance();
        var compass = DiagnosticToolsContent.COLTAN.get();
        double angle;
        // Source TextureColtass reads Minecraft.thePlayer, not the entity currently rendering a stack.
        if (client.level != null && client.player != null && !(compass.lastX == 0 && compass.lastZ == 0)
                && client.level.dimensionType().natural() && compass.lease > System.currentTimeMillis()) {
            double dx = (double) compass.lastX - client.player.getX();
            double dz = (double) compass.lastZ - client.player.getZ();
            double yaw = client.player.getYRot();
            yaw %= 360D;
            angle = -((yaw - 90D) * Math.PI / 180D - Math.atan2(dz, dx));
        } else {
            angle = Math.random() * Math.PI * 2D;
        }
        double difference;
        for (difference = angle - currentAngle; difference < -Math.PI; difference += Math.PI * 2D) { }
        while (difference >= Math.PI) difference -= Math.PI * 2D;
        if (difference < -1D) difference = -1D;
        if (difference > 1D) difference = 1D;
        angleDelta += difference * 0.1D;
        angleDelta *= 0.8D;
        currentAngle += angleDelta;
        int next = (int) ((currentAngle / (Math.PI * 2D) + 1D) * 32D) % 32;
        while (next < 0) next = (next + 32) % 32;
        frame = next;
    }

    @EventBusSubscriber(modid = "hbm", value = Dist.CLIENT)
    public static final class Ticks {
        @SubscribeEvent public static void tick(ClientTickEvent.Post event) { tickCompass(); }
        private Ticks() { }
    }
    private DiagnosticToolsClient() { }
}
