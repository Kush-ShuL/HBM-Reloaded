package com.hbm.client.armor.hev;

import com.hbm.armor.hev.HevArmorContent;
import com.hbm.armor.powered.SourcePoweredArmor;
import com.hbm.armor.t51.T51Runtime;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.exposure.HbmExposure;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/** Original ArmorHEV.handleOverlay/renderOverlay, at the native armor and health layers, not a screen overlay. */
@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT)
public final class HevHud {
    private static long lastSurvey;
    private static float prevResult, lastResult;
    @SubscribeEvent public static void overlay(RenderGuiLayerEvent.Pre event) {
        boolean armorLayer = VanillaGuiLayers.ARMOR_LEVEL.equals(event.getName());
        boolean healthLayer = VanillaGuiLayers.PLAYER_HEALTH.equals(event.getName());
        if (!armorLayer && !healthLayer) return;
        var mc = Minecraft.getInstance(); var player = mc.player;
        // Source overlay checks matching material, deliberately ignoring whether the four pieces have charge.
        if (player == null || mc.options.hideGui || !player.getItemBySlot(EquipmentSlot.CHEST).is(HevArmorContent.PLATE.get())
            || !T51Runtime.fullSet(player, false)) return;
        event.setCanceled(true);
        if (armorLayer) return;
        float dose = HbmExposure.radiation(player);
        float radiation = lastResult - prevResult; // Source computes delta before updating the once-per-second survey.
        long now = System.currentTimeMillis();
        if (now >= lastSurvey + 1000) { lastSurvey = now; prevResult = lastResult; lastResult = dose; }
        double chargeSum = 0;
        for (var slot : T51Runtime.ORDER) {
            var stack = player.getItemBySlot(slot); var powered = (SourcePoweredArmor) stack.getItem();
            chargeSum += (double) powered.getCharge(stack) / powered.getMaxCharge(stack);
        }
        var graphics = event.getGuiGraphics(); var font = LegacyFonts.get(); int height = graphics.guiHeight();
        // Flush inside the same native HUD layer, preserving117: screen/inventory overlays still render afterwards.
        graphics.flush(); graphics.pose().pushPose();
        try {
            graphics.pose().scale(2, 2, 2);
            int bottom = (int) ((height - 20) / 2D);
            graphics.drawString(font, "+" + (int) (player.getHealth() * 5), 4, bottom,
                player.getHealth() * 5 > 15 ? 0xff8000 : 0xff0000, false);
            graphics.drawString(font, "||" + (int) (chargeSum * 25), 35, bottom,
                chargeSum * 25 > 15 ? 0xff8000 : 0xff0000, false);
            var rad = new StringBuilder("☢ [");
            for (int i = 0; i < 10; i++) {
                if (dose / 100 > i) {
                    int remainder = (int) (dose - i * 100);
                    rad.append(remainder < 33 ? ".." : remainder < 67 ? "|." : "||");
                } else rad.append(' ');
            }
            rad.append(']');
            graphics.drawString(font, rad.toString(), 4, (int) ((height - 40) / 2D), dose < 800 ? 0xff8000 : 0xff0000, false);
            graphics.pose().scale(.5F, .5F, .5F);
            if (radiation > 0) {
                String delta = radiation > 1000 ? ">1000" : radiation < 1 ? "<1" : Long.toString(Math.round(radiation));
                graphics.drawString(font, delta + " RAD/s", 32, height - 55, 0xff0000, false);
            }
        } finally { graphics.flush(); graphics.pose().popPose(); }
    }
    private HevHud() { }
}
