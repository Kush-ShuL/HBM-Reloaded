package com.hbm.client.entity.glyphid.special;

import com.hbm.client.entity.glyphid.GlyphidModel;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.entity.mob.glyphid.special.EntityGlyphidNuclear;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/** The original Nuclear model differs only by this attached MiniNuke. Body/limbs remain the unique83 model. */
public final class GlyphidNuclearModel extends GlyphidModel<EntityGlyphidNuclear> {
    private static final ResourceLocation PROJECTILES = ResourceLocation.parse("hbm:models/projectiles/projectiles.obj");
    private static final ResourceLocation MINI_NUKE = ResourceLocation.parse("hbm:textures/models/projectiles/mini_nuke.png");
    private boolean colorPass;

    @Override protected void renderExtra(EntityGlyphidNuclear entity, PoseStack poses, MultiBufferSource buffers,
            VertexConsumer output, int light, int overlay, int color) {
        poses.pushPose();
        // Called inside original Rx180 / Ty-1.5 / sourceScale(2), after all articulated Glyphid parts.
        poses.translate(0, 1, 0);
        poses.mulPose(Axis.XP.rotationDegrees(90));
        var mesh = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), PROJECTILES);
        var vertices = colorPass ? output : buffers.getBuffer(RenderType.entityCutoutNoCull(MINI_NUKE));
        LegacyObjRenderer.renderOnly(mesh, poses, vertices, light, overlay, color, LegacyObjRenderer.Convention.HFR, "MiniNuke");
        poses.popPose();
    }
    void renderColorOverlay(PoseStack poses, VertexConsumer output, int light, int overlay, int color) {
        colorPass = true;
        try { super.renderToBuffer(poses, output, light, overlay, color); }
        finally { colorPass = false; }
    }
}
