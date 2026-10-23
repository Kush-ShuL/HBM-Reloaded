package com.hbm.client.armor.blackjack;

import com.hbm.armor.blackjack.ArmorBlackjack;
import com.hbm.client.model.LegacyItemTransforms;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** ArmorBJ's real ItemRenderBase/setupRenderInv/setupRenderNonInv/renderStandard matrices, including chest variant scale. */
public final class BlackjackItemRenderer extends BlockEntityWithoutLevelRenderer {
    public BlackjackItemRenderer() { super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()); }
    @Override public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poses, MultiBufferSource buffers, int light, int overlay) {
        if (!(stack.getItem() instanceof ArmorBlackjack item)) return;
        poses.pushPose();
        try {
            if (LegacyItemTransforms.isHeld(context)) LegacyItemTransforms.held(poses, context); else poses.translate(.5, .5, .5);
            if (context == ItemDisplayContext.GUI) {
                poses.scale(1F / 16F, -1F / 16F, 1F / 16F); poses.translate(0, 2, 0);
                poses.mulPose(Axis.XP.rotationDegrees(-30)); poses.mulPose(Axis.YP.rotationDegrees(45)); poses.scale(-1, -1, -1);
                poses.translate(0, -1.5, 0); poses.scale(3.25F, 3.25F, 3.25F);
                poses.mulPose(Axis.XP.rotationDegrees(180)); poses.mulPose(Axis.YP.rotationDegrees(-135)); poses.mulPose(Axis.XP.rotationDegrees(-20));
            } else {
                boolean entity = context == ItemDisplayContext.GROUND || context == ItemDisplayContext.FIXED || context == ItemDisplayContext.NONE;
                boolean equipped = context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                if (entity) poses.scale(1.5F, 1.5F, 1.5F); else poses.translate(.5, .25, 0);
                poses.scale(.25F, .25F, .25F); if (!equipped) poses.mulPose(Axis.YP.rotationDegrees(90));
                poses.mulPose(Axis.XP.rotationDegrees(180)); poses.scale(.75F, .75F, .75F); poses.mulPose(Axis.YP.rotationDegrees(-90));
            }
            switch (item.getEquipmentSlot()) {
                case HEAD -> {
                    poses.scale(.3125F, .3125F, .3125F); poses.translate(0, 1, 0);
                    draw(poses, buffers, light, overlay, "eyepatch", false, "Head");
                }
                case CHEST -> {
                    float scale = item.jetpack ? .6875F : .875F; poses.scale(scale, scale, scale);
                    poses.scale(.225F, .225F, .225F); poses.translate(0, -10, 0);
                    draw(poses, buffers, light, overlay, "chest", false, "Body");
                    poses.translate(0, 0, .1); draw(poses, buffers, light, overlay, "arm", false, "LeftArm", "RightArm");
                    if (item.jetpack) { poses.translate(0, 0, -.1); draw(poses, buffers, light, overlay, "jetpack", false, "Jetpack"); }
                }
                case LEGS -> {
                    poses.scale(.25F, .25F, .25F); poses.translate(0, -20, 0);
                    draw(poses, buffers, light, overlay, "leg", true, "LeftLeg"); poses.translate(0, 0, .1);
                    draw(poses, buffers, light, overlay, "leg", true, "RightLeg");
                }
                case FEET -> {
                    poses.scale(.25F, .25F, .25F); poses.translate(0, -22, 0);
                    draw(poses, buffers, light, overlay, "leg", true, "LeftFoot"); poses.translate(0, 0, .1);
                    draw(poses, buffers, light, overlay, "leg", true, "RightFoot");
                }
                default -> { }
            }
        } finally { poses.popPose(); }
    }
    private static void draw(PoseStack poses, MultiBufferSource buffers, int light, int overlay, String texture, boolean noCull, String... groups) {
        var mesh = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), BlackjackArmorRenderer.MODEL);
        ResourceLocation image = BlackjackArmorRenderer.texture(texture);
        LegacyObjRenderer.renderOnly(mesh, poses, buffers.getBuffer(noCull ? RenderType.entityCutoutNoCull(image) : RenderType.entityCutout(image)),
                light, overlay, -1, LegacyObjRenderer.Convention.HFR, groups);
    }
}
