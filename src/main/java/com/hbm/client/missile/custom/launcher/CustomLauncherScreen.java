package com.hbm.client.missile.custom.launcher;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.client.missile.custom.CustomMissileModels;
import com.hbm.fluid.FluidTank;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.missileassembly.MissileAssemblyContent;
import com.hbm.missile.custom.*;
import com.hbm.missile.custom.launcher.*;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.*;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

/** Source two176x222 screens, original slots/indicators/size selectors/tank bars and exact missile preview matrix. */
public final class CustomLauncherScreen extends LegacyContainerScreen<CustomLauncherMenu> {
    public CustomLauncherScreen(CustomLauncherMenu menu, Inventory player, Component title) { super(menu, player, title, MissileAssemblyContent.id("textures/gui/weapon/gui_launch_table" + (menu.large ? "" : "_small") + ".png"), 176, 222); }
    private CustomLauncherEntity machine() { return minecraft != null && minecraft.level != null && minecraft.level.getBlockEntity(menu.position()) instanceof CustomLauncherEntity machine ? machine : null; }
    @Override protected void renderLabels(GuiGraphics graphics, int x, int y) {
        graphics.drawString(font, title, 88 - font.width(title) / 2, 6, 4210752, false);
        graphics.drawString(font, playerInventoryTitle, 8, imageHeight - 96 + 2, 4210752, false);
    }
    @Override protected void renderBg(GuiGraphics graphics, float partial, int x, int y) {
        super.renderBg(graphics, partial, x, y); var machine = machine(); if (machine == null) return;
        int power = (int) (machine.power * 34 / CustomLauncherEntity.MAX_POWER), solid = machine.solid * 52 / machine.maxSolid;
        if (power > 0) drawLegacyRegion(graphics, 134, 113, 176, 96, power, 6);
        if (solid > 0) drawLegacyRegion(graphics, 152, 88 - solid, 176, 96 - solid, 16, solid);
        if (machine.isMissileValid()) drawLegacyRegion(graphics, 25, 35, 176, 26, 18, 18);
        if (machine.hasDesignator()) drawLegacyRegion(graphics, 25, 71, 176, 26, 18, 18);
        int[] states = {machine.liquidState(), machine.oxidizerState(), machine.solidState()};
        for (int i = 0; i < 3; i++) if (states[i] == 1 || states[i] == 0) drawLegacyRegion(graphics, 121 + i * 18, 23, states[i] == 1 ? 176 : 182, 0, 6, 8);
        if (menu.large) {
            int index = switch (machine.padSize) { case SIZE_10 -> 0; case SIZE_15 -> 1; case SIZE_20 -> 2; default -> -1; };
            if (index >= 0) drawLegacyRegion(graphics, 7 + index * 18, 98, 176 + index * 18, 8, 18, 18);
        }
        var utility = MissileAssemblyContent.id("textures/gui/gui_utility.png");
        graphics.blit(utility, leftPos - 16, topPos + 36, 8, 0, 16, 16, 256, 256);
        graphics.blit(utility, leftPos - 16, topPos + 52, 24, 32, 16, 16, 256, 256);
        tank(graphics, machine.tanks[0], 116); tank(graphics, machine.tanks[1], 134);
        if (machine.isMissileValid()) preview(graphics);
    }
    private void tank(GuiGraphics graphics, FluidTank tank, int offset) {
        int height = tank.getFill() * 34 / Math.max(tank.getMaxFill(), 1); if (height <= 0) return;
        graphics.flush(); int color = tank.getTankType().getTint();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader); RenderSystem.setShaderTexture(0, tank.getTankType().getTexture()); RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc();
        var vertices = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR); var matrix = graphics.pose().last().pose(); int x = leftPos + offset, y = topPos + 70;
        vertices.addVertex(matrix, x, y, 0).setUv(0, 1).setColor(color >> 16 & 255, color >> 8 & 255, color & 255, 255);
        vertices.addVertex(matrix, x + 16, y, 0).setUv(1, 1).setColor(color >> 16 & 255, color >> 8 & 255, color & 255, 255);
        vertices.addVertex(matrix, x + 16, y - height, 0).setUv(1, 1 - height / 16F).setColor(color >> 16 & 255, color >> 8 & 255, color & 255, 255);
        vertices.addVertex(matrix, x, y - height, 0).setUv(0, 1 - height / 16F).setColor(color >> 16 & 255, color >> 8 & 255, color & 255, 255);
        BufferUploader.drawWithShader(vertices.buildOrThrow()); RenderSystem.disableBlend();
    }
    private void preview(GuiGraphics graphics) {
        var missile = CustomMissileModels.fromStruct(CustomMissileItem.getStruct(menu.machine.getItem(0))); if (missile == null) return;
        graphics.flush(); Lighting.setupFor3DItems(); var poses = graphics.pose(); poses.pushPose();
        try {
            poses.translate(leftPos + 88, topPos + 115, 100); poses.mulPose(Axis.YP.rotationDegrees(90)); double scale = 90 / Math.max(missile.height(), 6);
            poses.translate(missile.height() / 2 * scale, 0, 0); poses.scale((float) -scale, (float) -scale, (float) -scale);
            CustomMissileModels.draw(missile, poses, graphics.bufferSource(), LightTexture.FULL_BRIGHT, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY); graphics.flush();
        } finally { poses.popPose(); RenderSystem.clear(256, net.minecraft.client.Minecraft.ON_OSX); Lighting.setupForFlatItems(); }
    }
    @Override public void render(GuiGraphics graphics, int x, int y, float partial) {
        super.render(graphics, x, y, partial); var machine = machine(); if (machine == null) return;
        for (int i = 0; i < 2; i++) if (legacyInfoHovered(x, y, 116 + 18 * i, 36, 16, 34)) {
            var tank = machine.tanks[i]; var lines = new ArrayList<Component>(); lines.add(tank.getTankType().displayName()); lines.add(Component.literal(tank.getFill() + " / " + tank.getMaxFill() + " mB"));
            FluidInfo.append(tank.getTankType(), lines, hasShiftDown()); graphics.renderComponentTooltip(font, lines, x, y);
        }
        if (legacyInfoHovered(x, y, 152, 36, 16, 52)) graphics.renderTooltip(font, Component.translatable("gui.hbm.custom_launcher.solid", machine.solid), x, y);
        if (legacyInfoHovered(x, y, 134, 113, 34, 6)) graphics.renderTooltip(font, Component.literal(String.format(Locale.US, "%,d / %,d HE", machine.power, CustomLauncherEntity.MAX_POWER)), x, y);
        if (legacyInfoHovered(x, y, -16, 36, 16, 16)) graphics.renderComponentTooltip(font, menu.large
                ? List.of(Component.translatable("gui.hbm.custom_launcher.large.1"), Component.translatable("gui.hbm.custom_launcher.large.2"), Component.translatable("gui.hbm.custom_launcher.large.3"))
                : List.of(Component.translatable("gui.hbm.custom_launcher.small.1"), Component.translatable("gui.hbm.custom_launcher.small.2")), x, y);
        if (legacyInfoHovered(x, y, -16, 52, 16, 16)) graphics.renderTooltip(font, Component.translatable("gui.hbm.custom_launcher.center"), x, y);
        if (menu.large) for (int index = 0; index < 3; index++) if (legacyInfoHovered(x, y, 7 + index * 18, 98, 18, 18)) graphics.renderTooltip(font, Component.translatable("gui.hbm.custom_launcher.size." + index), x, y);
    }
    @Override public boolean mouseClicked(double x, double y, int button) {
        boolean handled = super.mouseClicked(x, y, button);
        if (menu.large) for (int index = 0; index < 3; index++) if (legacyInfoHovered(x, y, 7 + index * 18, 98, 18, 18)) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1)); minecraft.gameMode.handleInventoryButtonClick(menu.containerId, index + 2); return true;
        }
        return handled;
    }
}
