package com.hbm.client.entity.aircraft.c130;

import com.hbm.client.model.*;
import com.hbm.entity.aircraft.c130.EntityC130;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/** Unchanged original C130 OBJ, authored normals and all four propeller pivots. */
public final class C130Renderer extends EntityRenderer<EntityC130> {
    private static final ResourceLocation MODEL = ResourceLocation.parse("hbm:models/weapons/c130.obj");
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("hbm:textures/models/weapons/c130_0.png");
    private static final double[] PROPELLERS = {-20.5, -11.16, 11.16, 20.5};
    public C130Renderer(EntityRendererProvider.Context context) { super(context); }
    @Override public ResourceLocation getTextureLocation(EntityC130 entity) { return TEXTURE; }
    @Override public boolean shouldRender(EntityC130 entity, Frustum frustum, double x, double y, double z) { return true; }
    @Override public void render(EntityC130 entity, float yaw, float partial, PoseStack poses, MultiBufferSource buffers, int light) {
        poses.pushPose();
        poses.mulPose(Axis.YP.rotationDegrees(entity.yRotO + (entity.getYRot() - entity.yRotO) * partial - 90F));
        poses.mulPose(Axis.ZP.rotationDegrees(90));
        poses.mulPose(Axis.ZP.rotationDegrees(entity.xRotO + (entity.getXRot() - entity.xRotO) * partial));
        var mesh = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), MODEL);
        var out = buffers.getBuffer(RenderType.entityCutout(TEXTURE));
        LegacyObjRenderer.renderOnly(mesh, poses, out, light, OverlayTexture.NO_OVERLAY, -1, LegacyObjRenderer.Convention.HFR, "Plane");
        double spin = System.currentTimeMillis() * 15D % 360D;
        for (int i = 0; i < PROPELLERS.length; i++) {
            double z = PROPELLERS[i];
            poses.pushPose();
            poses.translate(10, 4.2, z);
            poses.mulPose(Axis.XP.rotationDegrees((float) spin));
            poses.translate(-10, -4.2, -z);
            LegacyObjRenderer.renderOnly(mesh, poses, out, light, OverlayTexture.NO_OVERLAY, -1, LegacyObjRenderer.Convention.HFR, "Prop" + (i + 1));
            poses.popPose();
        }
        poses.popPose();
    }
}
