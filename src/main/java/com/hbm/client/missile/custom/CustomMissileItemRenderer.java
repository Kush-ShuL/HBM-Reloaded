package com.hbm.client.missile.custom;

import com.hbm.client.model.LegacyItemTransforms;
import com.hbm.missile.custom.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;
import static com.hbm.missile.custom.CustomMissileParts.PartType;

/** Source ItemRenderMissilePart and ItemRenderMissile have NO vanilla render helpers; retain their distinct pixel matrices. */
public final class CustomMissileItemRenderer extends BlockEntityWithoutLevelRenderer {
    public CustomMissileItemRenderer() { super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()); }
    @Override public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poses, MultiBufferSource buffers, int light, int overlay) {
        var part = CustomMissileModels.part(stack.getItem());
        var missile = stack.getItem() instanceof CustomMissileItem ? CustomMissileModels.fromStruct(CustomMissileItem.getStruct(stack)) : null;
        if (part == null && missile == null) return;
        poses.pushPose();
        try {
            if (LegacyItemTransforms.isHeld(context)) LegacyItemTransforms.held(poses, context); else poses.translate(.5, .5, .5);
            if (context == ItemDisplayContext.GUI) {
                // Forge IItemRenderer.INVENTORY's origin is the icon's top-left, without ItemRenderBase's extra helper rotation.
                poses.scale(1F / 16, -1F / 16, 1F / 16); poses.translate(-8, -8, 0);
                double height = part != null ? part.guiheight() : missile.height(); if (height == 0) height = 4;
                double scale = (part != null ? 10 : 20) / height;
                poses.translate(height / 2 * scale, 0, 0); poses.mulPose(Axis.ZP.rotationDegrees(135));
                poses.mulPose(Axis.XP.rotationDegrees(part != null ? 145 : 215));
                if (part != null && part.type() == PartType.WARHEAD) poses.translate(0, height / 8 * scale, 0);
                if (part != null && part.type() == PartType.FUSELAGE) poses.translate(0, height / 4 * scale, 0);
                poses.translate(part != null ? 3.5 : 7, 14, 0); poses.scale((float) -scale, (float) -scale, (float) -scale);
                poses.mulPose(Axis.YN.rotationDegrees(System.currentTimeMillis() / 25 % 360));
            } else if (part != null) {
                if (LegacyItemTransforms.isHeld(context)) poses.translate(.5, 0, 0); poses.scale(.4F, .4F, .4F);
            } else { poses.scale(.2F, .2F, .2F); poses.translate(2, 0, 0); }
            if (part != null) CustomMissileModels.draw(part, poses, buffers, light, overlay);
            else CustomMissileModels.draw(missile, poses, buffers, light, overlay);
        } finally { poses.popPose(); }
    }
}
