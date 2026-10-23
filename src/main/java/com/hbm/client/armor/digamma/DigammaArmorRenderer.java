package com.hbm.client.armor.digamma;

import com.hbm.armor.digamma.ArmorDigamma;
import com.hbm.armor.digamma.DigammaArmorContent;
import com.hbm.client.armor.t51.T51ArmorRenderer;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.client.model.WavefrontMesh;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

/** ModelArmorDigamma's nine authored groups and body-copied translucent cassette. */
public final class DigammaArmorRenderer {
    public static final ResourceLocation MODEL = DigammaArmorContent.id("models/armor/fau.obj");
    public static ResourceLocation texture(String part) { return DigammaArmorContent.id("textures/armor/fau_" + part + ".png"); }
    public static boolean render(PoseStack poses, MultiBufferSource buffers, int light, LivingEntity entity, EquipmentSlot slot, HumanoidModel<?> parent) {
        var stack = entity.getItemBySlot(slot);
        if (!(stack.getItem() instanceof ArmorDigamma armor) || armor.getEquipmentSlot() != slot) return false;
        var mesh = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), MODEL); boolean foil = stack.hasFoil();
        switch (slot) {
            case HEAD -> T51ArmorRenderer.part(mesh, poses, buffers, light, foil, texture("helmet"), parent.head, 0, 0, 0, 0, "Head");
            case CHEST -> {
                T51ArmorRenderer.part(mesh, poses, buffers, light, foil, texture("chest"), parent.body, 0, 0, 0, 0, "Body");
                // Source body.copyTo(cassette): exactly the same pivot/rotation, but its own skin and alpha blend.
                poses.pushPose();
                try {
                    var body = parent.body;
                    poses.translate(body.x / 16F, body.y / 16F, body.z / 16F);
                    if (body.zRot != 0) poses.mulPose(Axis.ZP.rotation(body.zRot));
                    if (body.yRot != 0) poses.mulPose(Axis.YP.rotation(body.yRot));
                    if (body.xRot != 0) poses.mulPose(Axis.XP.rotation(body.xRot));
                    poses.scale(1F / 16, 1F / 16, 1F / 16);
                    LegacyObjRenderer.renderOnly(mesh, poses,
                        ItemRenderer.getArmorFoilBuffer(buffers, RenderType.entityTranslucent(texture("cassette")), foil),
                        light, OverlayTexture.NO_OVERLAY, -1, LegacyObjRenderer.Convention.HFR, "Cassette");
                } finally { poses.popPose(); }
                T51ArmorRenderer.part(mesh, poses, buffers, light, foil, texture("arm"), parent.leftArm, 5, 2, 0, 0, "LeftArm");
                T51ArmorRenderer.part(mesh, poses, buffers, light, foil, texture("arm"), parent.rightArm, -5, 2, 0, 0, "RightArm");
            }
            case LEGS -> {
                T51ArmorRenderer.part(mesh, poses, buffers, light, foil, texture("leg"), parent.leftLeg, 1.9F, 12, 0, 0, "LeftLeg");
                T51ArmorRenderer.part(mesh, poses, buffers, light, foil, texture("leg"), parent.rightLeg, -1.9F, 12, 0, 0, "RightLeg");
            }
            case FEET -> {
                T51ArmorRenderer.part(mesh, poses, buffers, light, foil, texture("leg"), parent.leftLeg, 1.9F, 12, 0, 0, "LeftBoot");
                T51ArmorRenderer.part(mesh, poses, buffers, light, foil, texture("leg"), parent.rightLeg, -1.9F, 12, 0, 0, "RightBoot");
            }
            default -> { }
        }
        return true;
    }
    private DigammaArmorRenderer() { }
}
