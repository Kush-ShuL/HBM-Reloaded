package com.hbm.client.reactor.rbmk.residue;

import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.machine.reactor.rbmk.residue.RbmkDebrisEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

/** Original six Forge Wavefront models and original per-entity/diagonal rotations. */
public final class RbmkDebrisRenderer extends EntityRenderer<RbmkDebrisEntity> {
    public RbmkDebrisRenderer(EntityRendererProvider.Context context) { super(context); shadowRadius = 0; }
    private static String part(RbmkDebrisEntity debris) { return debris.debrisType().name().toLowerCase(java.util.Locale.ROOT); }
    @Override public ResourceLocation getTextureLocation(RbmkDebrisEntity debris) {
        return ResourceLocation.parse("hbm:textures/blocks/" + switch (debris.debrisType()) {
            case BLANK -> "rbmk/rbmk_blank_side.png";
            case ELEMENT -> "rbmk/rbmk_side.png";
            case FUEL -> "rbmk/rbmk_fuel.png";
            case GRAPHITE -> "block_graphite.png";
            case LID -> "rbmk/rbmk_blank_cover_top.png";
            case ROD -> "rbmk/rbmk_control.png";
        });
    }
    @Override public void render(RbmkDebrisEntity debris, float yaw, float partial, PoseStack poses, MultiBufferSource buffers, int light) {
        poses.pushPose(); poses.translate(0, .125, 0);
        poses.mulPose(Axis.YP.rotationDegrees(debris.getId() % 360));
        poses.mulPose(Axis.of(new Vector3f(1, 1, 1).normalize()).rotationDegrees(debris.lastRotation + (debris.rotation - debris.lastRotation) * partial));
        var mesh = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), ResourceLocation.parse("hbm:models/projectiles/deb_" + part(debris) + ".obj"));
        LegacyObjRenderer.render(mesh, poses, buffers.getBuffer(RenderType.entityCutout(getTextureLocation(debris))), light, OverlayTexture.NO_OVERLAY, -1, LegacyObjRenderer.Convention.FORGE, face -> true);
        poses.popPose(); super.render(debris, yaw, partial, poses, buffers, light);
    }
}
