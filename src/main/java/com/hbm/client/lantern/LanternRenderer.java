package com.hbm.client.lantern;

import com.hbm.block.lantern.LanternEntity;
import com.hbm.client.model.LegacyContactShadows;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

/** Original ordinary Lantern/Light parts, no metadata yaw and original wall-clock yellow lamp pulse. */
public final class LanternRenderer implements BlockEntityRenderer<LanternEntity> {
    private static final ResourceLocation MODEL = ResourceLocation.parse("hbm:models/trinkets/lantern.obj");
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("hbm:textures/models/trinkets/lantern.png");
    private static final ResourceLocation WHITE = ResourceLocation.parse("hbm:textures/models/trinkets/lantern90_white.png");
    public LanternRenderer(BlockEntityRendererProvider.Context context) { }
    @Override public void render(LanternEntity lantern, float partial, PoseStack poses, MultiBufferSource buffers, int light, int overlay) {
        var mesh = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), MODEL);
        LegacyContactShadows.render(lantern, mesh, new Matrix4f().translation(.5F, 0, .5F), poses, buffers);
        poses.pushPose(); poses.translate(.5, 0, .5);
        draw(poses, buffers, light, overlay, true);
        poses.popPose();
    }
    /** Source world disables lightmap/lighting for Light; source item common does not disable lighting. */
    static void draw(PoseStack poses, MultiBufferSource buffers, int light, int overlay, boolean world) {
        var mesh = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), MODEL);
        var body = buffers.getBuffer(world ? RenderType.entityCutoutNoCull(TEXTURE) : RenderType.entityCutout(TEXTURE));
        LegacyObjRenderer.renderOnly(mesh, poses, body, light, overlay, -1, LegacyObjRenderer.Convention.HFR_FLAT, "Lantern");
        float mult = (float)(Math.sin(System.currentTimeMillis() / 200D) / 2 + .5) * .1F + .9F;
        int color = 0xFF000000 | ((int)(255 * mult) << 16) | ((int)(255 * mult) << 8) | (int)(255 * .7F * mult);
        var lamp = world ? LanternRenderTypes.colorOnly(buffers.getBuffer(LanternRenderTypes.LIGHT))
                : buffers.getBuffer(RenderType.entityCutout(WHITE));
        LegacyObjRenderer.renderOnly(mesh, poses, lamp, world ? LightTexture.FULL_BRIGHT : light, overlay, color,
                LegacyObjRenderer.Convention.HFR_FLAT, "Light");
    }
    @Override public int getViewDistance() { return 256; }
    @Override public AABB getRenderBoundingBox(LanternEntity lantern) {
        var pos = lantern.getBlockPos();
        return new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 6, pos.getZ() + 1);
    }
}
