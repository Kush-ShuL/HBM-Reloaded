package com.hbm.client.missileassembly;

import com.hbm.client.model.LegacyItemTransforms;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;

/** Original ItemRenderLibrary395..403 ItemRenderBase matrix and no-cull assembly machine mesh. */
public final class MissileAssemblyItemRenderer extends BlockEntityWithoutLevelRenderer {
    public MissileAssemblyItemRenderer() { super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()); }
    @Override public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poses, MultiBufferSource buffers, int light, int overlay) {
        poses.pushPose();
        try {
            if (LegacyItemTransforms.isHeld(context)) LegacyItemTransforms.held(poses, context); else poses.translate(.5, .5, .5);
            if (context == ItemDisplayContext.GUI) {
                poses.scale(1F / 16, -1F / 16, 1F / 16); poses.translate(0, 2, 0); poses.mulPose(Axis.XP.rotationDegrees(-30));
                poses.mulPose(Axis.YP.rotationDegrees(45)); poses.scale(-1, -1, -1); poses.translate(0, -2.5, 0); poses.scale(10, 10, 10);
            } else {
                boolean entity = context == ItemDisplayContext.GROUND || context == ItemDisplayContext.FIXED || context == ItemDisplayContext.NONE;
                if (entity) poses.scale(1.5F, 1.5F, 1.5F); else poses.translate(.5, .25, 0);
                poses.scale(.25F, .25F, .25F);
                if (context != ItemDisplayContext.THIRD_PERSON_LEFT_HAND && context != ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) poses.mulPose(Axis.YP.rotationDegrees(90));
            }
            MissileAssemblyRenderer.body(poses, buffers, light, overlay);
        } finally { poses.popPose(); }
    }
}
