package com.hbm.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.world.item.ItemDisplayContext;

/** Caller transforms around X5846 ItemRenderBase, before that renderer's own transforms. */
public final class LegacyItemTransforms {
    private LegacyItemTransforms() { }

    public static boolean isHeld(ItemDisplayContext context) {
        return context.firstPerson() || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND
                || context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
    }

    public static void held(PoseStack poses, ItemDisplayContext context) {
        held(poses, context, false);
    }

    /** For source ItemRenderers on ordinary full-3D items, also declining EQUIPPED_BLOCK. */
    public static void heldFull3D(PoseStack poses, ItemDisplayContext context) {
        held(poses, context, true);
    }

    private static void held(PoseStack poses, ItemDisplayContext context, boolean full3D) {
        if (!isHeld(context)) return;
        // ItemInHandRenderer already positions and swings the physical first-person
        // arm. X5846's model-local equipped matrices were authored in one right-hand
        // basis; reversing just this bridge leaves their later offsets unmirrored
        // and can place the whole model behind the camera. Keep that original basis
        // in both first-person arms; third-person retains its existing arm adapter.
        int hand = context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND ? -1 : 1;

        // ItemRenderer moves builtin/entity models to the block origin. Legacy
        // IItemRenderer instead received the caller's equipped origin directly.
        poses.translate(.5, .5, .5);
        if (context.firstPerson()) {
            // Both versions use the same idle/swing hand path, but the modern
            // path cancels its initial 45-degree rotation and omits the .4 scale.
            poses.mulPose(Axis.YP.rotationDegrees(hand * 45));
            poses.scale(.4F, .4F, .4F);
        } else {
            // Undo ItemInHandLayer, retaining the animated arm's matrix.
            poses.translate(-hand / 16D, -.125, .625);
            poses.mulPose(Axis.YP.rotationDegrees(-180));
            poses.mulPose(Axis.XP.rotationDegrees(90));

            // RenderPlayer's non-full-3D item branch in 1.7.10. The original
            // ItemRenderBase declines both BLOCK_3D and EQUIPPED_BLOCK helpers.
            poses.translate(-hand / 16D, .4375, .0625);
            if (full3D) {
                poses.translate(0, .1875, 0);
                poses.scale(.625F, -.625F, .625F);
                poses.mulPose(Axis.XP.rotationDegrees(-100));
                poses.mulPose(Axis.YP.rotationDegrees(hand * 45));
            } else {
                poses.translate(hand * .25, .1875, -.1875);
                poses.scale(.375F, .375F, .375F);
                poses.mulPose(Axis.ZP.rotationDegrees(hand * 60));
                poses.mulPose(Axis.XP.rotationDegrees(-90));
                poses.mulPose(Axis.ZP.rotationDegrees(hand * 20));
            }
        }

        // ForgeHooksClient.renderEquippedItem, EQUIPPED_BLOCK == false.
        poses.translate(0, -.3, 0);
        poses.scale(1.5F, 1.5F, 1.5F);
        poses.mulPose(Axis.YP.rotationDegrees(hand * 50));
        poses.mulPose(Axis.ZP.rotationDegrees(hand * 335));
        poses.translate(hand * -.9375, -.0625, 0);
    }
}
