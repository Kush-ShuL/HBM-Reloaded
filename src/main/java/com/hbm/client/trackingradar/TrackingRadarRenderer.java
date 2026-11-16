package com.hbm.client.trackingradar;

import com.hbm.client.model.*;
import com.hbm.machine.trackingradar.TrackingRadarContent.RadarEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

public final class TrackingRadarRenderer implements BlockEntityRenderer<RadarEntity> {
    private static final ResourceLocation MODEL = ResourceLocation.parse("hbm:models/machines/tracking_radar.obj");
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("hbm:textures/models/machines/tracking_radar.png");
    public TrackingRadarRenderer(BlockEntityRendererProvider.Context context) { }
    static void common(float mountYaw, float lift, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        var mesh = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), MODEL);
        var vertices = buffers.getBuffer(RenderType.entityCutout(TEXTURE));
        LegacyObjRenderer.renderOnly(mesh, pose, vertices, light, overlay, -1, LegacyObjRenderer.Convention.HFR, "Base");
        pose.mulPose(Axis.YP.rotationDegrees(mountYaw));
        LegacyObjRenderer.renderOnly(mesh, pose, vertices, light, overlay, -1, LegacyObjRenderer.Convention.HFR, "Mount");
        pose.translate(0, 9.375, 0); pose.mulPose(Axis.XP.rotationDegrees(lift+90)); pose.translate(0, -9.375, 0);
        LegacyObjRenderer.renderOnly(mesh, pose, vertices, light, overlay, -1, LegacyObjRenderer.Convention.HFR, "Dish");
    }
    @Override public void render(RadarEntity radar, float partial, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        pose.pushPose(); pose.translate(.5, 0, .5); pose.mulPose(Axis.YP.rotationDegrees(90));
        common(net.minecraft.util.Mth.lerp(partial,radar.prevRot,radar.rot),net.minecraft.util.Mth.lerp(partial,radar.prevLift,radar.lift),pose,buffers,light,overlay); pose.popPose();
    }
    @Override public boolean shouldRenderOffScreen(RadarEntity radar) { return true; }
    @Override public int getViewDistance() { return 256; }
    @Override public AABB getRenderBoundingBox(RadarEntity radar) { var p = radar.getBlockPos(); return new AABB(p.getX()-5, p.getY(), p.getZ()-5, p.getX()+6, p.getY()+16, p.getZ()+6); }
}
