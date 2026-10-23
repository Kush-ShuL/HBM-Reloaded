package com.hbm.client.missileassembly;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.client.missile.custom.CustomMissileModels;
import com.hbm.machine.missileassembly.*;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

/** Exact original176x222 GUI, six status sprites/button and source rotating real missile preview. */
public final class MissileAssemblyScreen extends LegacyContainerScreen<MissileAssemblyMenu> {
    public MissileAssemblyScreen(MissileAssemblyMenu menu, Inventory player, Component title) { super(menu, player, title, MissileAssemblyContent.id("textures/gui/gui_missile_assembly.png"), 176, 222); }
    @Override protected void renderBg(GuiGraphics graphics, float partial, int mouseX, int mouseY) {
        super.renderBg(graphics, partial, mouseX, mouseY); var slots = menu.machine;
        int[] states = {MissileAssemblyRules.chip(slots), MissileAssemblyRules.warhead(slots), MissileAssemblyRules.fuselage(slots), MissileAssemblyRules.stability(slots), MissileAssemblyRules.thruster(slots)};
        for (int slot = 0; slot < 5; slot++) if (states[slot] == 1) drawLegacyRegion(graphics, 13 + slot * 18, 23, 194, 0, 6, 8);
        if (states[3] == 0) drawLegacyRegion(graphics, 67, 23, 200, 0, 6, 8);
        if (MissileAssemblyRules.canBuild(slots)) drawLegacyRegion(graphics, 115, 35, 176, 0, 18, 18);
        var missile = CustomMissileModels.fromSlots(slots); var poses = graphics.pose(); graphics.flush(); Lighting.setupFor3DItems();
        poses.pushPose();
        try {
            poses.translate(leftPos + 88, topPos + 98, 100); poses.mulPose(Axis.YN.rotationDegrees(System.currentTimeMillis() / 10 % 360));
            double scale = 144 / Math.max(missile.height(), 6);
            poses.translate(missile.height() / 2 * scale, 0, 0); poses.scale((float) scale, (float) scale, (float) scale);
            poses.mulPose(Axis.XP.rotationDegrees(90)); poses.mulPose(Axis.ZP.rotationDegrees(-90)); poses.scale(-1, -1, -1);
            CustomMissileModels.draw(missile, poses, graphics.bufferSource(), 0xF000F0, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);
            graphics.flush();
        } finally {
            poses.popPose();
            // End miniature3D depth at its boundary so later native slots/foreground retain the source GUI ordering.
            RenderSystem.clear(256, net.minecraft.client.Minecraft.ON_OSX); Lighting.setupForFlatItems();
        }
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, imageWidth / 2 - font.width(title) / 2, 6, 4210752, false);
        graphics.drawString(font, playerInventoryTitle, 8, imageHeight - 96 + 2, 4210752, false);
    }
    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean handled = super.mouseClicked(mouseX, mouseY, button);
        if (legacyInfoHovered(mouseX, mouseY, 115, 35, 18, 18)) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0); return true;
        }
        return handled;
    }
}
