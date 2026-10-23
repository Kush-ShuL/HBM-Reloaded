package com.hbm.client.entity.aircraft.c130;

import com.hbm.client.model.*;
import com.hbm.entity.aircraft.c130.EntityParachuteCrate;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/** Source conservecrate and soyuz Chute group, seven-block swing pivot and original tick clock. */
public final class ParachuteCrateRenderer extends EntityRenderer<EntityParachuteCrate> {
    private static final ResourceLocation CRATE = ResourceLocation.parse("hbm:models/blocks/conservecrate.obj");
    private static final ResourceLocation LANDER = ResourceLocation.parse("hbm:models/soyuz_lander.obj");
    private static final ResourceLocation CRATE_SKIN = ResourceLocation.parse("hbm:textures/block/crate_can.png");
    private static final ResourceLocation CHUTE_SKIN = ResourceLocation.parse("hbm:textures/models/soyuz_capsule/soyuz_chute.png");
    private static final ResourceLocation LANDER_SKIN = ResourceLocation.parse("hbm:textures/models/soyuz_capsule/soyuz_lander.png");
    public ParachuteCrateRenderer(EntityRendererProvider.Context context) { super(context); }
    @Override public ResourceLocation getTextureLocation(EntityParachuteCrate entity) { return LANDER_SKIN; }
    @Override public boolean shouldRender(EntityParachuteCrate entity, Frustum frustum, double x, double y, double z) { return true; }
    @Override public void render(EntityParachuteCrate entity, float yaw, float partial, PoseStack poses, MultiBufferSource buffers, int light) {
        double time = entity.level().getGameTime();
        poses.pushPose(); poses.translate(0, 7, 0);
        poses.mulPose(Axis.ZP.rotationDegrees((float) (Math.sin(time * .05) * 5)));
        poses.mulPose(Axis.XP.rotationDegrees((float) (Math.sin(time * .05 + Math.PI * .5) * 5)));
        poses.translate(0, -7, 0);
        var resources = Minecraft.getInstance().getResourceManager();
        LegacyObjRenderer.renderAll(LegacyObjModels.INSTANCE.get(resources, CRATE), poses, buffers.getBuffer(RenderType.entityCutout(CRATE_SKIN)), light, OverlayTexture.NO_OVERLAY, -1, LegacyObjRenderer.Convention.HFR_FLAT);
        poses.translate(0, -1, 0);
        LegacyObjRenderer.renderOnly(LegacyObjModels.INSTANCE.get(resources, LANDER), poses, buffers.getBuffer(RenderType.entityCutout(CHUTE_SKIN)), light, OverlayTexture.NO_OVERLAY, -1, LegacyObjRenderer.Convention.HFR, "Chute");
        poses.popPose();
    }
}
