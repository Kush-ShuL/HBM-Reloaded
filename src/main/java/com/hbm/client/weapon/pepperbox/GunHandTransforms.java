package com.hbm.client.weapon.pepperbox;

import com.hbm.client.model.LegacyObjRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.world.item.ItemDisplayContext;

/** Actual arm wrist followed by the complete, right-authored X5846 equipped path. */
public final class GunHandTransforms {
    public static LegacyObjRenderer.Reflection held(PoseStack poses, ItemDisplayContext context) {
        return held(poses, context, false);
    }
    /** Original RenderPlayer full-3D sword branch; existing gun branch remains unchanged. */
    public static LegacyObjRenderer.Reflection heldFull3D(PoseStack poses, ItemDisplayContext context) {
        return held(poses, context, true);
    }
    private static LegacyObjRenderer.Reflection held(PoseStack poses, ItemDisplayContext context, boolean full3D) {
        boolean third = context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
        if (!third) {
            boolean left = context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
            // External previews still enter through ItemRenderer's -.5 origin.
            if (left) poses.translate(.5, .5, .5);
            var reflection = LegacyObjRenderer.mirrorX(poses, left);
            if (left) poses.translate(-.5, -.5, -.5);
            return reflection;
        }
        int arm = context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND ? -1 : 1;
        poses.translate(.5, .5, .5);
        // Undo only ItemInHandLayer's item adapter, retaining this arm's actual pose.
        poses.translate(-arm / 16D, -.125, .625);
        poses.mulPose(Axis.YP.rotationDegrees(-180));
        poses.mulPose(Axis.XP.rotationDegrees(90));
        poses.translate(-arm / 16D, .4375, .0625);

        // A reflection belongs before every original item/model transform. Changing
        // only the caller's angles leaves the source gun's later grip offset behind.
        var reflection = LegacyObjRenderer.mirrorX(poses, arm < 0);
        if (full3D) {
            poses.translate(0, .1875, 0);
            poses.scale(.625F, -.625F, .625F);
            poses.mulPose(Axis.XP.rotationDegrees(-100));
            poses.mulPose(Axis.YP.rotationDegrees(45));
        } else {
            poses.translate(.25, .1875, -.1875);
            poses.scale(.375F, .375F, .375F);
            poses.mulPose(Axis.ZP.rotationDegrees(60));
            poses.mulPose(Axis.XP.rotationDegrees(-90));
            poses.mulPose(Axis.ZP.rotationDegrees(20));
        }
        poses.translate(0, -.3, 0);
        poses.scale(1.5F, 1.5F, 1.5F);
        poses.mulPose(Axis.YP.rotationDegrees(50));
        poses.mulPose(Axis.ZP.rotationDegrees(335));
        poses.translate(-.9375, -.0625, 0);
        return reflection;
    }
    private GunHandTransforms() { }
}
