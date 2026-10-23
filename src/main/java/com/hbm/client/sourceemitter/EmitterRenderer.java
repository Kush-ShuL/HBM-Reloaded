package com.hbm.client.sourceemitter;

import com.hbm.block.sourceemitter.*;
import com.hbm.client.reactor.dfc.components.DfcComponentBeams;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.awt.Color;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.world.phys.AABB;

/** Exact RenderEmitter matrices and BeamPronter parameters, through the one complete shared beam kernel. */
public final class EmitterRenderer implements BlockEntityRenderer<EmitterEntity> {
    public EmitterRenderer(BlockEntityRendererProvider.Context context) { }
    @Override public void render(EmitterEntity emitter, float partial, PoseStack poses, MultiBufferSource buffers, int light, int overlay) {
        var level = emitter.getLevel(); if (level == null) return;
        int range = emitter.beam - 1; if (range <= 0) return;
        poses.pushPose();
        try {
            poses.translate(.5, 0, .5); poses.mulPose(Axis.YP.rotationDegrees(90));
            switch (emitter.getBlockState().getValue(EmitterBlock.META)) {
                case 0 -> { poses.translate(0, .5, -.5); poses.mulPose(Axis.XP.rotationDegrees(90)); }
                case 1 -> { poses.translate(0, .5, .5); poses.mulPose(Axis.XN.rotationDegrees(90)); }
                case 2 -> poses.mulPose(Axis.YP.rotationDegrees(90));
                case 4 -> poses.mulPose(Axis.YP.rotationDegrees(180));
                case 3 -> poses.mulPose(Axis.YP.rotationDegrees(270));
                default -> { }
            }
            poses.translate(0, .5, .5);
            int rgb = emitter.color == 0 ? Color.HSBtoRGB(level.getGameTime() / 50.0F, .5F, .25F) & 0xffffff : emitter.color;
            int r = rgb >> 16 & 255, g = rgb >> 8 & 255, b = rgb & 255;
            int inner = (int) (r * .85F) << 16 | (int) (g * .85F) << 8 | (int) (b * .85F);
            int outer = (int) (r * .1F) << 16 | (int) (g * .1F) << 8 | (int) (b * .1F);
            float girth = emitter.girth;
            int layers = (int) Math.max(Math.sqrt(girth * 50), 2);
            DfcComponentBeams.draw(poses, buffers, (double) range, false, true, outer, inner, 0, 1, 0F, layers, girth);
            int phase = (int) (level.getGameTime() + partial) * -10 % 360;
            int spiralSegments = (int) Math.max(range / girth / 2, 1);
            if (emitter.effect == 1) {
                DfcComponentBeams.draw(poses, buffers, (double) range, true, true, outer, inner, (int) level.getGameTime() / 2, spiralSegments, girth * 2, 4, girth * .1F);
                DfcComponentBeams.draw(poses, buffers, (double) range, true, true, outer, inner, (int) level.getGameTime() / 2 + 15, (int) Math.max(range / girth / 4, 1), girth * 2, 4, girth * .1F);
            }
            if (emitter.effect == 2 || emitter.effect == 3) {
                int count = emitter.effect == 2 ? 2 : 3;
                for (int i = 0; i < count; i++)
                    DfcComponentBeams.draw(poses, buffers, (double) range, false, true, outer, inner, phase + i * (360 / count), spiralSegments, girth * 2, 4, girth * .1F);
            }
        } finally { poses.popPose(); }
    }
    @Override public boolean shouldRenderOffScreen(EmitterEntity emitter) { return true; }
    @Override public AABB getRenderBoundingBox(EmitterEntity emitter) { return AABB.INFINITE; }
    @Override public int getViewDistance() { return 256; }
}
