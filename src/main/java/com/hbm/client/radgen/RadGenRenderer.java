package com.hbm.client.radgen;

import com.hbm.client.model.*;
import com.hbm.machine.radgen.RadGenEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

/** Exact source HFR groups, wall-clock rotor, untextured30% glass then original textured opaque Glass pass. */
public final class RadGenRenderer implements BlockEntityRenderer<RadGenEntity> {
    static final ResourceLocation MODEL = ResourceLocation.parse("hbm:models/machines/radgen.obj");
    static final ResourceLocation TEXTURE = ResourceLocation.parse("hbm:textures/models/machines/radgen.png");
    public RadGenRenderer(BlockEntityRendererProvider.Context context) {}
    private static WavefrontMesh model() { return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), MODEL); }
    private static void group(String name, PoseStack pose, MultiBufferSource buffers, RenderType type, int light, int overlay, int color, boolean colorOnly) {
        var vertices = buffers.getBuffer(type);
        LegacyObjRenderer.renderOnly(model(), pose, colorOnly ? RadGenRenderTypes.colorOnly(vertices) : vertices, light, overlay, color, LegacyObjRenderer.Convention.HFR, name);
    }
    private static void flush(MultiBufferSource buffers, RenderType type) { if (buffers instanceof MultiBufferSource.BufferSource source) source.endBatch(type); }
    static void item(PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        var type = RenderType.entityCutout(TEXTURE);
        group("Base", pose, buffers, type, light, overlay, -1, false);
        group("Rotor", pose, buffers, type, light, overlay, -1, false);
        flush(buffers, type);
        // Source item render leaves lighting on, unlike the real world's fullbright indicator.
        group("Light", pose, buffers, RadGenRenderTypes.ITEM_LIGHT, light, overlay, 0xff00ff00, false);
        flush(buffers, RadGenRenderTypes.ITEM_LIGHT);
        // ItemRenderLibrary138 deliberately does not render either Glass pass.
    }
    private static float yaw(RadGenEntity machine) {
        return switch (DummyableBlock.facing(machine.getBlockState())) { case NORTH -> 90; case WEST -> 180; case SOUTH -> 270; default -> 0; };
    }
    @Override public void render(RadGenEntity machine, float partial, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        float yaw = yaw(machine);
        LegacyContactShadows.render(machine, model(), new Matrix4f().translation(.5F, 0, .5F).rotateY((float) Math.toRadians(yaw)), pose, buffers);
        pose.pushPose();
        pose.translate(.5, 0, .5);
        pose.mulPose(Axis.YP.rotationDegrees(yaw));
        var type = RenderType.entityCutoutNoCull(TEXTURE);
        group("Base", pose, buffers, type, light, overlay, -1, false);
        pose.pushPose();
        if (machine.isOn) { pose.translate(0, 1.5, 0); pose.mulPose(Axis.XP.rotationDegrees((System.currentTimeMillis() % 3600) * -.1F)); pose.translate(0, -1.5, 0); }
        group("Rotor", pose, buffers, type, light, overlay, -1, false);
        pose.popPose();
        flush(buffers, type);
        group("Light", pose, buffers, RadGenRenderTypes.LIGHT, LightTexture.FULL_BRIGHT, overlay, machine.isOn ? 0xff00ff00 : 0xff001a00, true);
        flush(buffers, RadGenRenderTypes.LIGHT);
        group("Glass", pose, buffers, RadGenRenderTypes.GLASS, light, overlay, 0x4d80bfff, false);
        flush(buffers, RadGenRenderTypes.GLASS);
        group("Glass", pose, buffers, type, light, overlay, -1, false);
        flush(buffers, type);
        pose.popPose();
    }
    @Override public boolean shouldRenderOffScreen(RadGenEntity machine) { return true; }
    @Override public int getViewDistance() { return 256; }
}
