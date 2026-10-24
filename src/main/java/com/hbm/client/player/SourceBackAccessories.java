package com.hbm.client.player;

import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.client.render.WorldEffectMeshes;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/** Original ModelArmorWingsPheo and ModelArmorTailPeep, in their caller's player model frame. */
public final class SourceBackAccessories {
    private static final ResourceLocation AXE_MODEL = ResourceLocation.parse("hbm:models/armor/wings_pheo.obj");
    private static final ResourceLocation AXE_TEXTURE = ResourceLocation.parse("hbm:textures/armor/axepack.png");
    private static final ResourceLocation TAIL_MODEL = ResourceLocation.parse("hbm:models/armor/tail_peep.obj");
    private static final ResourceLocation TAIL_TEXTURE = ResourceLocation.parse("hbm:textures/armor/tail_peep.png");
    private static final double PIXEL = 1D / 16D;
    private static final double WIDTH = .0625D, BASE = .125D, TIP = .375D;
    private static final double DIAGONAL = Math.sqrt(2D) / 2D * WIDTH;
    private static final double[][] RING = {
            {WIDTH, 0}, {DIAGONAL, DIAGONAL}, {0, WIDTH}, {-DIAGONAL, DIAGONAL},
            {-WIDTH, 0}, {-DIAGONAL, -DIAGONAL}, {0, -WIDTH}, {DIAGONAL, -DIAGONAL}
    };

    public static void renderAxe(PoseStack poses, MultiBufferSource buffers, int light,
            AbstractClientPlayer player, PlayerModel<AbstractClientPlayer> parent) {
        model(poses, buffers, light, parent, AXE_MODEL, AXE_TEXTURE, "Wings");

        // Unlike the solid axe, source flames inherit only this explicit sneaking tilt.
        poses.pushPose();
        try {
            if (player.isCrouching()) poses.mulPose(Axis.XP.rotationDegrees(28.6479F));
            var vertices = buffers.getBuffer(FlameRenderType.FLAME);
            baseFlame(poses, vertices);
            sideFlames(poses, vertices, -25, -90);
            // X5846 renders this identical base flame twice; additive contribution is intentional.
            baseFlame(poses, vertices);
            sideFlames(poses, vertices, 25, 90);
        } finally {
            poses.popPose();
        }
    }

    public static void renderTail(PoseStack poses, MultiBufferSource buffers, int light,
            AbstractClientPlayer player, PlayerModel<AbstractClientPlayer> parent) {
        model(poses, buffers, light, parent, TAIL_MODEL, TAIL_TEXTURE, "FaggyAssFuckingTailThing");
    }

    private static void model(PoseStack poses, MultiBufferSource buffers, int light,
            PlayerModel<AbstractClientPlayer> parent, ResourceLocation location, ResourceLocation texture, String group) {
        var mesh = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), location);
        poses.pushPose();
        try {
            parent.body.translateAndRotate(poses);
            poses.scale(1F / 16F, 1F / 16F, 1F / 16F);
            LegacyObjRenderer.renderOnly(mesh, poses, buffers.getBuffer(RenderType.entityCutoutNoCull(texture)),
                    light, OverlayTexture.NO_OVERLAY, -1, LegacyObjRenderer.Convention.HFR, group);
        } finally {
            poses.popPose();
        }
    }

    private static void baseFlame(PoseStack poses, VertexConsumer vertices) {
        poses.pushPose();
        try {
            poses.translate(0, PIXEL * 15, PIXEL * 5.5);
            renderFlame(poses, vertices);
        } finally {
            poses.popPose();
        }
    }

    private static void sideFlames(PoseStack poses, VertexConsumer vertices, float yaw, float roll) {
        poses.pushPose();
        try {
            poses.translate(0, PIXEL * 3, PIXEL * 5.5);
            poses.mulPose(Axis.YP.rotationDegrees(yaw));
            poses.mulPose(Axis.ZP.rotationDegrees(roll));
            poses.translate(0, PIXEL * 5, 0);
            renderFlame(poses, vertices);

            poses.pushPose();
            try {
                poses.translate(0, -PIXEL * 5, 0);
                poses.mulPose(Axis.ZP.rotationDegrees(45));
                poses.translate(-PIXEL, PIXEL * 5.5, 0);
                renderFlame(poses, vertices);
            } finally {
                poses.popPose();
            }

            poses.pushPose();
            try {
                poses.translate(0, -PIXEL * 5, 0);
                poses.mulPose(Axis.ZP.rotationDegrees(-45));
                poses.translate(PIXEL, PIXEL * 5.5, 0);
                renderFlame(poses, vertices);
            } finally {
                poses.popPose();
            }
        } finally {
            poses.popPose();
        }
    }

    private static void renderFlame(PoseStack poses, VertexConsumer vertices) {
        // Keep source triangle order: eight base triangles followed by eight tip triangles.
        for (int i = 0; i < RING.length; i++) {
            vertex(poses, vertices, 0, 0, 0, 0x808080);
            vertex(poses, vertices, RING[i][0], BASE, RING[i][1], 0x004040);
            int next = (i + 1) % RING.length;
            vertex(poses, vertices, RING[next][0], BASE, RING[next][1], 0x004040);
        }
        for (int i = 0; i < RING.length; i++) {
            vertex(poses, vertices, 0, TIP, 0, 0x000000);
            vertex(poses, vertices, RING[i][0], BASE, RING[i][1], 0x004040);
            int next = (i + 1) % RING.length;
            vertex(poses, vertices, RING[next][0], BASE, RING[next][1], 0x004040);
        }
    }

    private static void vertex(PoseStack poses, VertexConsumer vertices, double x, double y, double z, int rgb) {
        vertices.addVertex(poses.last(), (float)x, (float)y, (float)z).setColor(0xff000000 | rgb);
    }

    private static final class FlameRenderType extends RenderType {
        private static final TransparencyStateShard SOURCE_ADD = new TransparencyStateShard("hbm_pheo_source_add",
                () -> {
                    RenderSystem.enableBlend();
                    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                }, () -> {
                    RenderSystem.defaultBlendFunc();
                    RenderSystem.disableBlend();
                });
        private static final RenderType FLAME = WorldEffectMeshes.register(create("hbm_pheo_source_flames",
                DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES, 4096, false, false,
                CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setTransparencyState(SOURCE_ADD)
                        .setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST)
                        .setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false)),
                "ModelArmorWingsPheo/renderFlame: eight source draws, unlit SRC_ALPHA/ONE, depth writes");

        private FlameRenderType() {
            super("unused", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES, 256,
                    false, false, () -> {}, () -> {});
        }
    }

    private SourceBackAccessories() {}
}
