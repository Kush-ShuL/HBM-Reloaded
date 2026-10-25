package com.hbm.client.player;

import com.hbm.client.render.WorldEffectMeshes;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

/** Original amplified-invisibility player suppression and the named-player two-unit billboard. */
@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT)
public final class SourcePlayerRenderEvents {
    private static final net.minecraft.client.renderer.MultiBufferSource.BufferSource BILLBOARD_BUFFERS =
            net.minecraft.client.renderer.MultiBufferSource.immediate(new com.mojang.blaze3d.vertex.ByteBufferBuilder(256));
    // Cancel before ordinary model-part snapshots, which require a matching RenderPlayerEvent.Post.
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void beforePlayer(RenderPlayerEvent.Pre event) {
        var player = event.getEntity();
        var invisibility = player.getEffect(MobEffects.INVISIBILITY);
        if (invisibility != null && invisibility.getAmplifier() > 0) event.setCanceled(true);
        if (!player.getDisplayName().getString().toLowerCase(Locale.US).equals("martmn")) return;
        event.setCanceled(true);
        var local = Minecraft.getInstance().player;
        if (local == null) return;
        var pose = event.getPoseStack();
        pose.pushPose();
        try {
            // Native entity render origins are feet; the old eye-position minus yOffset is the same origin.
            pose.translate(0, .75, 0);
            pose.mulPose(Axis.YP.rotationDegrees(-local.getYRot()));
            pose.mulPose(Axis.XP.rotationDegrees(local.getXRot()));
            // Flush this owned quad before Iris beginTranslucents, so its real opaque plane supplies early depth.
            var vertices = BILLBOARD_BUFFERS.getBuffer(BillboardMaterial.TYPE);
            var matrix = pose.last();
            vertices.addVertex(matrix.pose(), -1, 1, 0).setColor(-1).setUv(0, 0)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(matrix, 0, 0, 1);
            vertices.addVertex(matrix.pose(), 1, 1, 0).setColor(-1).setUv(1, 0)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(matrix, 0, 0, 1);
            vertices.addVertex(matrix.pose(), 1, -1, 0).setColor(-1).setUv(1, 1)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(matrix, 0, 0, 1);
            vertices.addVertex(matrix.pose(), -1, -1, 0).setColor(-1).setUv(0, 1)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(matrix, 0, 0, 1);
            BILLBOARD_BUFFERS.endBatch(BillboardMaterial.TYPE);
        } finally {
            pose.popPose();
        }
    }

    private static final class BillboardMaterial extends RenderType {
        private BillboardMaterial() { super("unused", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false, () -> {}, () -> {}); }
        private static final RenderType TYPE = WorldEffectMeshes.occluder(WorldEffectMeshes.<RenderType>register(create("hbm_source_player_billboard",
                DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false,
                CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                        .setTextureState(new TextureStateShard(ResourceLocation.parse("hbm:textures/particle/fart.png"), false, false))
                        .setTransparencyState(NO_TRANSPARENCY).setCullState(NO_CULL)
                        .setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(com.hbm.client.render.SourceDepthWrite.MASK)
                        .createCompositeState(false)), "OriginalPlayerBillboard"));
    }

    private SourcePlayerRenderEvents() {}
}
