package com.hbm.client.soyuz;

import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.world.soyuz.EntitySoyuzCapsule;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/** Original RenderSoyuzCapsule's seven-block pendulum pivot, partial clock and two separately bound groups. */
public final class SoyuzDescentRenderer extends EntityRenderer<EntitySoyuzCapsule> {
    public SoyuzDescentRenderer(EntityRendererProvider.Context context) { super(context); }
    @Override public ResourceLocation getTextureLocation(EntitySoyuzCapsule entity) { return SoyuzCapsuleRenderer.NORMAL; }
    @Override public void render(EntitySoyuzCapsule entity, float yaw, float partial, PoseStack pose, MultiBufferSource buffers, int light) {
        double time = (entity.level().getGameTime() * .05) % (Math.PI * 2) + partial * .05D;
        pose.pushPose(); pose.translate(0, 7, 0);
        pose.mulPose(Axis.ZP.rotationDegrees((float) (Math.sin(time) * 5)));
        pose.mulPose(Axis.XP.rotationDegrees((float) (Math.sin(time + Math.PI * .5) * 5)));
        pose.translate(0, -7, 0);
        SoyuzCapsuleRenderer.capsule(pose, buffers, light, OverlayTexture.NO_OVERLAY, false);
        LegacyObjRenderer.renderOnly(SoyuzCapsuleRenderer.model(), pose, buffers.getBuffer(RenderType.entityCutout(SoyuzCapsuleRenderer.CHUTE)),
                light, OverlayTexture.NO_OVERLAY, -1, LegacyObjRenderer.Convention.HFR, "Chute");
        pose.popPose();
    }
}
