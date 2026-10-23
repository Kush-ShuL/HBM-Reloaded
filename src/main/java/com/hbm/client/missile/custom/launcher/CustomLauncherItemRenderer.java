package com.hbm.client.missile.custom.launcher;

import com.hbm.client.model.LegacyItemTransforms;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;

/** Original ItemRenderLibrary two entries: common halfscale and source eight-segment small table scaffold. */
public final class CustomLauncherItemRenderer extends BlockEntityWithoutLevelRenderer {
    private final boolean large;
    public CustomLauncherItemRenderer(boolean large) { super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()); this.large = large; }
    @Override public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poses, MultiBufferSource buffers, int light, int overlay) {
        poses.pushPose();
        try {
            if (LegacyItemTransforms.isHeld(context)) LegacyItemTransforms.held(poses, context); else poses.translate(.5, .5, .5);
            if (context == ItemDisplayContext.GUI) {
                poses.scale(1F / 16, -1F / 16, 1F / 16); poses.translate(0, 2, 0); poses.mulPose(Axis.XP.rotationDegrees(-30)); poses.mulPose(Axis.YP.rotationDegrees(45)); poses.scale(-1, -1, -1);
                poses.translate(0, large ? -2 : -4, 0); float scale = large ? 2.5F : 3.5F; poses.scale(scale, scale, scale);
            } else {
                boolean entity = context == ItemDisplayContext.GROUND || context == ItemDisplayContext.FIXED || context == ItemDisplayContext.NONE;
                if (entity) poses.scale(1.5F, 1.5F, 1.5F); else poses.translate(.5, .25, 0);
                poses.scale(.25F, .25F, .25F); if (context != ItemDisplayContext.THIRD_PERSON_LEFT_HAND && context != ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) poses.mulPose(Axis.YP.rotationDegrees(90));
            }
            poses.scale(.5F, .5F, .5F); CustomLauncherModels.draw(large ? "launch_table_base" : "compact_launcher", poses, buffers, light, overlay);
            if (large) {
                CustomLauncherModels.draw("launch_table_small_pad", poses, buffers, light, overlay); poses.translate(0, 0, 2.5);
                for (int i = 0; i < 8; i++) { poses.translate(0, 1, 0); CustomLauncherModels.draw("launch_table_small_scaffold_" + (i < 6 ? "base" : i == 6 ? "connector" : "empty"), poses, buffers, light, overlay); }
            }
        } finally { poses.popPose(); }
    }
}
