package com.hbm.client.soyuz;

import com.hbm.client.model.LegacyItemTransforms;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;

/** ItemRenderLibrary449: its own inventory T(0,-4,0)/S5; common draws unrotated, unrusted Capsule. */
public final class SoyuzCapsuleItemRenderer extends BlockEntityWithoutLevelRenderer {
    public SoyuzCapsuleItemRenderer() { super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()); }
    @Override public void renderByItem(ItemStack item, ItemDisplayContext context, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        pose.pushPose();
        if (LegacyItemTransforms.isHeld(context)) LegacyItemTransforms.held(pose, context); else pose.translate(.5, .5, .5);
        if (context == ItemDisplayContext.GUI) {
            pose.scale(1F / 16, -1F / 16, 1F / 16); pose.translate(0, 2, 0);
            pose.mulPose(Axis.XP.rotationDegrees(-30)); pose.mulPose(Axis.YP.rotationDegrees(45)); pose.scale(-1, -1, -1);
            pose.translate(0, -4, 0); pose.scale(5, 5, 5);
        } else {
            boolean entity = context == ItemDisplayContext.GROUND || context == ItemDisplayContext.FIXED || context == ItemDisplayContext.NONE;
            if (entity) pose.scale(1.5F, 1.5F, 1.5F); else pose.translate(.5, .25, 0);
            pose.scale(.25F, .25F, .25F);
            if (context != ItemDisplayContext.THIRD_PERSON_LEFT_HAND && context != ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) pose.mulPose(Axis.YP.rotationDegrees(90));
        }
        SoyuzCapsuleRenderer.capsule(pose, buffers, light, overlay, false);
        pose.popPose();
    }
}
