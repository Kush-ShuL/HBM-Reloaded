package com.hbm.client.armor.trenchmaster;

import com.hbm.armor.trenchmaster.*;
import com.hbm.client.armor.t51.T51ArmorRenderer;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;

/** Original ModelArmorTrenchmaster nine groups, transparent helmet, fullbright Light, and +/-0.01 leg offsets. */
public final class TrenchmasterArmorRenderer {
    public static final ResourceLocation MODEL = TrenchmasterContent.id("models/armor/trenchmaster.obj");
    public static final ResourceLocation HEAD = texture("helmet"), CHEST = texture("chest"), ARM = texture("arm"), LEG = texture("leg");
    private static ResourceLocation texture(String part) { return TrenchmasterContent.id("textures/armor/trenchmaster_" + part + ".png"); }

    public static boolean render(PoseStack poses, MultiBufferSource buffers, int light, LivingEntity entity,
                                 EquipmentSlot slot, HumanoidModel<?> parent) {
        var stack = entity.getItemBySlot(slot);
        if (!(stack.getItem() instanceof ArmorTrenchmaster item) || item.getEquipmentSlot() != slot) return false;
        var mesh = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), MODEL);
        boolean foil = stack.hasFoil();
        switch (slot) {
            case HEAD -> helmet(mesh, poses, buffers, light, foil, parent);
            case CHEST -> {
                T51ArmorRenderer.part(mesh, poses, buffers, light, foil, CHEST, parent.body, 0, 0, 0, 0, "Chest");
                T51ArmorRenderer.part(mesh, poses, buffers, light, foil, ARM, parent.leftArm, 5, 2, 0, 0, "LeftArm");
                T51ArmorRenderer.part(mesh, poses, buffers, light, foil, ARM, parent.rightArm, -5, 2, 0, 0, "RightArm");
            }
            case LEGS -> {
                T51ArmorRenderer.part(mesh, poses, buffers, light, foil, LEG, parent.leftLeg, 1.9F, 12, 0, -.01, "LeftLeg");
                T51ArmorRenderer.part(mesh, poses, buffers, light, foil, LEG, parent.rightLeg, -1.9F, 12, 0, .01, "RightLeg");
            }
            case FEET -> {
                T51ArmorRenderer.part(mesh, poses, buffers, light, foil, LEG, parent.leftLeg, 1.9F, 12, 0, -.01, "LeftBoot");
                T51ArmorRenderer.part(mesh, poses, buffers, light, foil, LEG, parent.rightLeg, -1.9F, 12, 0, .01, "RightBoot");
            }
            default -> { }
        }
        return true;
    }
    private static void helmet(WavefrontMesh mesh, PoseStack poses, MultiBufferSource buffers,
                               int light, boolean foil, HumanoidModel<?> parent) {
        poses.pushPose();
        try {
            var head = parent.head;
            poses.translate(head.x / 16F, head.y / 16F, head.z / 16F);
            if (head.zRot != 0) poses.mulPose(Axis.ZP.rotation(head.zRot));
            if (head.yRot != 0) poses.mulPose(Axis.YP.rotation(head.yRot));
            if (head.xRot != 0) poses.mulPose(Axis.XP.rotation(head.xRot));
            poses.scale(1F / 16, 1F / 16, 1F / 16);
            LegacyObjRenderer.renderOnly(mesh, poses, ItemRenderer.getArmorFoilBuffer(buffers, RenderType.entityTranslucent(HEAD), foil),
                    light, OverlayTexture.NO_OVERLAY, -1, LegacyObjRenderer.Convention.HFR, "Helmet");
            LegacyObjRenderer.renderOnly(mesh, poses, buffers.getBuffer(net.neoforged.neoforge.client.NeoForgeRenderTypes.getUnlitTranslucent(HEAD, false)),
                    0xF000F0, OverlayTexture.NO_OVERLAY, -1, LegacyObjRenderer.Convention.HFR, "Light");
        } finally { poses.popPose(); }
    }
    private TrenchmasterArmorRenderer() {}
}
