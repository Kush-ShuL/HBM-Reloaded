package com.hbm.client.soyuz;

import com.hbm.client.model.*;
import com.hbm.world.soyuz.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

/** Source RenderCapsule: T(.5,-.25,.5), RY(-25), RZ(15), smooth Capsule only. */
public final class SoyuzCapsuleRenderer implements BlockEntityRenderer<SoyuzCapsuleBlockEntity> {
    static final ResourceLocation MODEL = ResourceLocation.parse("hbm:models/soyuz_lander.obj");
    static final ResourceLocation NORMAL = ResourceLocation.parse("hbm:textures/models/soyuz_capsule/soyuz_lander.png");
    static final ResourceLocation RUST = ResourceLocation.parse("hbm:textures/models/soyuz_capsule/soyuz_lander_rust.png");
    static final ResourceLocation CHUTE = ResourceLocation.parse("hbm:textures/models/soyuz_capsule/soyuz_chute.png");
    public SoyuzCapsuleRenderer(BlockEntityRendererProvider.Context context) { }
    static WavefrontMesh model() { return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), MODEL); }
    static void capsule(PoseStack pose, MultiBufferSource buffers, int light, int overlay, boolean rust) {
        LegacyObjRenderer.renderOnly(model(), pose, buffers.getBuffer(RenderType.entityCutout(rust ? RUST : NORMAL)),
                light, overlay, -1, LegacyObjRenderer.Convention.HFR, "Capsule");
    }
    @Override public void render(SoyuzCapsuleBlockEntity capsule, float partial, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        LegacyContactShadows.render(capsule, model(), new Matrix4f().translation(.5F, -.25F, .5F)
                .rotateY((float) Math.toRadians(-25)).rotateZ((float) Math.toRadians(15)), pose, buffers);
        pose.pushPose(); pose.translate(.5, -.25, .5);
        pose.mulPose(Axis.YP.rotationDegrees(-25)); pose.mulPose(Axis.ZP.rotationDegrees(15));
        capsule(pose, buffers, light, overlay, capsule.getBlockState().getValue(SoyuzCapsuleBlock.META) == 3);
        pose.popPose();
    }
    @Override public AABB getRenderBoundingBox(SoyuzCapsuleBlockEntity capsule) {
        var p = capsule.getBlockPos(); return new AABB(p.getX() - 1, p.getY() - 1, p.getZ() - 1, p.getX() + 2, p.getY() + 3, p.getZ() + 2);
    }
}
