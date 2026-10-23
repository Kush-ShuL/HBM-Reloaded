package com.hbm.client.armor.digamma;

import com.hbm.armor.digamma.ArmorDigamma;
import com.hbm.client.model.LegacyItemTransforms;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.client.weapon.pepperbox.GunHandTransforms;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** Original ArmorDigamma item matrices, including the helmet-only GUI adjustment and chest-textured item cassette. */
public final class DigammaItemRenderer extends BlockEntityWithoutLevelRenderer {
    public DigammaItemRenderer() { super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()); }
    @Override public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poses, MultiBufferSource buffers, int light, int overlay) {
        if (!(stack.getItem() instanceof ArmorDigamma item)) return;
        poses.pushPose();
        try (var hand = GunHandTransforms.held(poses, context)) {
            if (context.firstPerson()) LegacyItemTransforms.held(poses, context);
            else if (!LegacyItemTransforms.isHeld(context)) poses.translate(.5, .5, .5);
            if (context == ItemDisplayContext.GUI) {
                poses.scale(1F / 16, -1F / 16, 1F / 16); poses.translate(0, 2, 0);
                poses.mulPose(Axis.XP.rotationDegrees(-30)); poses.mulPose(Axis.YP.rotationDegrees(45)); poses.scale(-1, -1, -1);
                if (item.getEquipmentSlot() == net.minecraft.world.entity.EquipmentSlot.HEAD) { poses.scale(.875F, .875F, .875F); poses.translate(0, -2, 0); }
                poses.translate(0, -1.5, 0); poses.scale(3.25F, 3.25F, 3.25F);
                poses.mulPose(Axis.XP.rotationDegrees(180)); poses.mulPose(Axis.YP.rotationDegrees(-135)); poses.mulPose(Axis.XP.rotationDegrees(-20));
            } else {
                boolean dropped = context == ItemDisplayContext.GROUND || context == ItemDisplayContext.FIXED || context == ItemDisplayContext.NONE;
                boolean third = context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                if (dropped) poses.scale(1.5F, 1.5F, 1.5F); else poses.translate(.5, .25, 0);
                poses.scale(.25F, .25F, .25F);
                if (!third) poses.mulPose(Axis.YP.rotationDegrees(90));
                poses.mulPose(Axis.XP.rotationDegrees(180)); poses.scale(.75F, .75F, .75F); poses.mulPose(Axis.YP.rotationDegrees(-90));
            }
            switch (item.getEquipmentSlot()) {
                case HEAD -> {
                    poses.scale(.3125F, .3125F, .3125F); poses.translate(0, 1, 0);
                    draw(poses, buffers, light, overlay, DigammaArmorRenderer.texture("helmet"), false, "Head");
                }
                case CHEST -> {
                    poses.scale(.225F, .225F, .225F); poses.translate(0, -10, 0);
                    draw(poses, buffers, light, overlay, DigammaArmorRenderer.texture("chest"), false, "Body", "Cassette");
                    poses.translate(0, 0, .1); draw(poses, buffers, light, overlay, DigammaArmorRenderer.texture("arm"), false, "LeftArm", "RightArm");
                }
                case LEGS -> {
                    poses.scale(.25F, .25F, .25F); poses.translate(0, -20, 0);
                    draw(poses, buffers, light, overlay, DigammaArmorRenderer.texture("leg"), true, "LeftLeg");
                    poses.translate(0, 0, .1); draw(poses, buffers, light, overlay, DigammaArmorRenderer.texture("leg"), true, "RightLeg");
                }
                case FEET -> {
                    poses.scale(.25F, .25F, .25F); poses.translate(0, -22, 0);
                    draw(poses, buffers, light, overlay, DigammaArmorRenderer.texture("leg"), true, "LeftBoot");
                    poses.translate(0, 0, .1); draw(poses, buffers, light, overlay, DigammaArmorRenderer.texture("leg"), true, "RightBoot");
                }
                default -> { }
            }
        } finally { poses.popPose(); }
    }
    private static void draw(PoseStack poses, MultiBufferSource buffers, int light, int overlay, ResourceLocation texture, boolean noCull, String... parts) {
        var mesh = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), DigammaArmorRenderer.MODEL);
        LegacyObjRenderer.renderOnly(mesh, poses, buffers.getBuffer(noCull ? RenderType.entityCutoutNoCull(texture) : RenderType.entityCutout(texture)),
            light, overlay, -1, LegacyObjRenderer.Convention.HFR, parts);
    }
}
