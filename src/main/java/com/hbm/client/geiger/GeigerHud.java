package com.hbm.client.geiger;

import com.hbm.HbmReloaded;
import com.hbm.client.config.LegacyClientConfig;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.exposure.HbmExposure;
import com.hbm.geiger.GeigerContent;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/** Original ModEventHandlerClient HOTBAR Pre and RenderScreenOverlay.renderRadCounter. */
@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT)
public final class GeigerHud {
    private static final ResourceLocation TEXTURE = HbmReloaded.id("textures/misc/overlay_misc.png");
    private static long lastSurvey;
    private static float previousResult, lastResult;
    private GeigerHud() {}

    @SubscribeEvent public static void beforeHotbar(RenderGuiLayerEvent.Pre event) {
        if (!event.getName().equals(VanillaGuiLayers.HOTBAR)) return;
        var client = Minecraft.getInstance();
        var player = client.player;
        if (client.options.hideGui || player == null || client.level == null || HbmExposure.usesCustomGeigerHud(player)) return;
        if (player.getInventory().items.stream().noneMatch(stack -> stack.is(GeigerContent.HANDHELD.get()))) return;

        float bodyDose = HbmExposure.radiation(player);
        // The old renderer deliberately calculates its delta before the next survey.
        float radiation = lastResult - previousResult;
        long now = System.currentTimeMillis();
        if (now >= lastSurvey + 1000) {
            lastSurvey = now;
            previousResult = lastResult;
            lastResult = bodyDose;
        }
        int bar = (int) Math.min(bodyDose / 1000D * 74D, 74D);
        var graphics = event.getGuiGraphics();
        int x = 16 + LegacyClientConfig.geigerHorizontal();
        int y = graphics.guiHeight() - 20 - LegacyClientConfig.geigerVertical();
        graphics.blit(TEXTURE, x, y, 0, 0, 94, 18, 256, 256);
        graphics.blit(TEXTURE, x + 1, y + 1, 1, 19, bar, 16, 256, 256);
        if (radiation >= 2.5F) {
            int u = radiation >= 25 ? 36 : radiation >= 10 ? 18 : 0;
            graphics.blit(TEXTURE, x + 76, y - 18, u, 36, 18, 18, 256, 256);
        }
        String rate = radiation > 1000 ? ">1000 RAD/s" : radiation >= 1 ? Math.round(radiation) + " RAD/s" : radiation > 0 ? "<1 RAD/s" : null;
        if (rate != null) graphics.drawString(LegacyFonts.get(), rate, x, y - 8, 0xFF0000, false);
    }
}
