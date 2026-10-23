package com.hbm.client.missile.custom;

import com.hbm.entity.missile.CustomMissileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;

/** Exact original three authored rotations and multipart order; never a fixed-missile model substitution. */
public final class CustomMissileEntityRenderer extends EntityRenderer<CustomMissileEntity> {
    public CustomMissileEntityRenderer(EntityRendererProvider.Context context) { super(context); }
    @Override public ResourceLocation getTextureLocation(CustomMissileEntity entity) { return ResourceLocation.parse("hbm:textures/models/universal.png"); }
    @Override public void render(CustomMissileEntity entity, float yaw, float partial, PoseStack poses, MultiBufferSource buffers, int light) {
        float ry = entity.yRotO + (entity.getYRot() - entity.yRotO) * partial - 90, rx = entity.xRotO + (entity.getXRot() - entity.xRotO) * partial;
        poses.pushPose(); try {
            poses.mulPose(Axis.YP.rotationDegrees(ry)); poses.mulPose(Axis.ZP.rotationDegrees(rx)); poses.mulPose(Axis.YN.rotationDegrees(ry));
            CustomMissileModels.draw(CustomMissileModels.fromStruct(entity.parts()), poses, buffers, light, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);
        } finally { poses.popPose(); }
        super.render(entity, yaw, partial, poses, buffers, light);
    }
}
