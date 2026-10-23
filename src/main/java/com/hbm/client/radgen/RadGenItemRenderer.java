package com.hbm.client.radgen;

import com.hbm.client.model.LegacyItemTransforms;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;

/** Original ItemRenderLibrary138: GUI T(0,-1,0)/S4.5; common S.5 then T(.5,0,0). */
public final class RadGenItemRenderer extends BlockEntityWithoutLevelRenderer {
    public RadGenItemRenderer() { super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()); }
    @Override public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        pose.pushPose();
        if (LegacyItemTransforms.isHeld(context)) LegacyItemTransforms.held(pose, context); else pose.translate(.5, .5, .5);
        if (context == ItemDisplayContext.GUI) {
            pose.scale(1F / 16, -1F / 16, 1F / 16); pose.translate(0, 2, 0);
            pose.mulPose(Axis.XP.rotationDegrees(-30)); pose.mulPose(Axis.YP.rotationDegrees(45)); pose.scale(-1, -1, -1);
            pose.translate(0, -1, 0); pose.scale(4.5F, 4.5F, 4.5F);
        } else {
            boolean entity = context == ItemDisplayContext.GROUND || context == ItemDisplayContext.FIXED || context == ItemDisplayContext.NONE;
            if (entity) pose.scale(1.5F, 1.5F, 1.5F); else pose.translate(.5, .25, 0);
            pose.scale(.25F, .25F, .25F);
            if (context != ItemDisplayContext.THIRD_PERSON_LEFT_HAND && context != ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) pose.mulPose(Axis.YP.rotationDegrees(90));
        }
        pose.scale(.5F, .5F, .5F); pose.translate(.5, 0, 0);
        RadGenRenderer.item(pose, buffers, light, overlay);
        pose.popPose();
    }
}
