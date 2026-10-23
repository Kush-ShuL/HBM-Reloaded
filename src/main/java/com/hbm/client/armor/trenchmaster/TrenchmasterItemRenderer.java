package com.hbm.client.armor.trenchmaster;

import com.hbm.armor.trenchmaster.ArmorTrenchmaster;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;

/** Original ItemRenderBase + ArmorFSB matrix; GUI head/chest shifts occur BEFORE setupRenderInv. */
public final class TrenchmasterItemRenderer extends BlockEntityWithoutLevelRenderer {
    public TrenchmasterItemRenderer() { super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()); }
    @Override public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poses,
                                       MultiBufferSource buffers, int light, int overlay) {
        if (!(stack.getItem() instanceof ArmorTrenchmaster item)) return;
        var slot = item.getEquipmentSlot();
        poses.pushPose();
        try {
            if (LegacyItemTransforms.isHeld(context)) LegacyItemTransforms.held(poses, context);
            else poses.translate(.5, .5, .5);
            if (context == ItemDisplayContext.GUI) {
                poses.scale(1F / 16F, -1F / 16F, 1F / 16F);
                poses.translate(0, 2, 0);
                poses.mulPose(Axis.XP.rotationDegrees(-30));
                poses.mulPose(Axis.YP.rotationDegrees(45));
                poses.scale(-1, -1, -1);
                if (slot == EquipmentSlot.HEAD) poses.translate(0, 1, 0);
                if (slot == EquipmentSlot.CHEST) poses.translate(0, 1.5, 0);
                poses.translate(0, -1.5, 0);
                poses.scale(3.25F, 3.25F, 3.25F);
                poses.mulPose(Axis.XP.rotationDegrees(180));
                poses.mulPose(Axis.YP.rotationDegrees(-135));
                poses.mulPose(Axis.XP.rotationDegrees(-20));
            } else {
                boolean entity = context == ItemDisplayContext.GROUND || context == ItemDisplayContext.FIXED || context == ItemDisplayContext.NONE;
                boolean equipped = context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                if (entity) poses.scale(1.5F, 1.5F, 1.5F);
                else poses.translate(.5, .25, 0);
                poses.scale(.25F, .25F, .25F);
                if (!equipped) poses.mulPose(Axis.YP.rotationDegrees(90));
                poses.mulPose(Axis.XP.rotationDegrees(180));
                poses.scale(.75F, .75F, .75F);
                poses.mulPose(Axis.YP.rotationDegrees(-90));
            }
            switch (slot) {
                case HEAD -> {
                    poses.scale(.3125F, .3125F, .3125F); poses.translate(0, 1, 0);
                    // The original item Light is ordinarily lit; only the worn model forces fullbrightness.
                    draw(poses, buffers, light, overlay, TrenchmasterArmorRenderer.HEAD, false, "Helmet", "Light");
                }
                case CHEST -> {
                    poses.scale(.225F, .225F, .225F); poses.translate(0, -10, 0);
                    draw(poses, buffers, light, overlay, TrenchmasterArmorRenderer.CHEST, false, "Chest");
                    poses.translate(0, 0, .1);
                    draw(poses, buffers, light, overlay, TrenchmasterArmorRenderer.ARM, false, "LeftArm", "RightArm");
                }
                case LEGS -> {
                    poses.scale(.25F, .25F, .25F); poses.translate(0, -20, 0);
                    draw(poses, buffers, light, overlay, TrenchmasterArmorRenderer.LEG, true, "LeftLeg");
                    poses.translate(0, 0, .1); draw(poses, buffers, light, overlay, TrenchmasterArmorRenderer.LEG, true, "RightLeg");
                }
                case FEET -> {
                    poses.scale(.25F, .25F, .25F); poses.translate(0, -22, 0);
                    draw(poses, buffers, light, overlay, TrenchmasterArmorRenderer.LEG, true, "LeftBoot");
                    poses.translate(0, 0, .1); draw(poses, buffers, light, overlay, TrenchmasterArmorRenderer.LEG, true, "RightBoot");
                }
                default -> { }
            }
        } finally { poses.popPose(); }
    }
    private static void draw(PoseStack poses, MultiBufferSource buffers, int light, int overlay,
                             ResourceLocation texture, boolean noCull, String... groups) {
        var mesh = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), TrenchmasterArmorRenderer.MODEL);
        LegacyObjRenderer.renderOnly(mesh, poses, buffers.getBuffer(noCull ? RenderType.entityCutoutNoCull(texture) : RenderType.entityCutout(texture)),
                light, overlay, -1, LegacyObjRenderer.Convention.HFR, groups);
    }
}
