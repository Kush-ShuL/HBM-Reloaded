package com.hbm.client.armor.blackjack;

import com.hbm.armor.blackjack.ArmorBlackjack;
import com.hbm.armor.blackjack.BlackjackContent;
import com.hbm.client.armor.t51.T51ArmorRenderer;
import com.hbm.client.model.LegacyObjModels;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

/** Original ModelArmorBJ's nine groups, five textures and exact shared body/Jetpack pivot. */
public final class BlackjackArmorRenderer {
    public static final ResourceLocation MODEL = BlackjackContent.id("models/armor/bj.obj");
    public static ResourceLocation texture(String part) { return BlackjackContent.id("textures/armor/bj_" + part + ".png"); }
    public static boolean render(PoseStack poses, MultiBufferSource buffers, int light, LivingEntity entity, EquipmentSlot slot, HumanoidModel<?> parent) {
        var stack = entity.getItemBySlot(slot);
        if (!(stack.getItem() instanceof ArmorBlackjack item) || item.getEquipmentSlot() != slot) return false;
        var mesh = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), MODEL);
        boolean foil = stack.hasFoil();
        switch (slot) {
            case HEAD -> T51ArmorRenderer.part(mesh, poses, buffers, light, foil, texture("eyepatch"), parent.head, 0, 0, 0, 0, "Head");
            case CHEST -> {
                T51ArmorRenderer.part(mesh, poses, buffers, light, foil, texture("chest"), parent.body, 0, 0, 0, 0, "Body");
                if (item.jetpack) T51ArmorRenderer.part(mesh, poses, buffers, light, foil, texture("jetpack"), parent.body, 0, 0, 0, 0, "Jetpack");
                T51ArmorRenderer.part(mesh, poses, buffers, light, foil, texture("arm"), parent.leftArm, 5, 2, 0, 0, "LeftArm");
                T51ArmorRenderer.part(mesh, poses, buffers, light, foil, texture("arm"), parent.rightArm, -5, 2, 0, 0, "RightArm");
            }
            case LEGS -> {
                T51ArmorRenderer.part(mesh, poses, buffers, light, foil, texture("leg"), parent.leftLeg, 1.9F, 12, 0, 0, "LeftLeg");
                T51ArmorRenderer.part(mesh, poses, buffers, light, foil, texture("leg"), parent.rightLeg, -1.9F, 12, 0, 0, "RightLeg");
            }
            case FEET -> {
                T51ArmorRenderer.part(mesh, poses, buffers, light, foil, texture("leg"), parent.leftLeg, 1.9F, 12, 0, 0, "LeftFoot");
                T51ArmorRenderer.part(mesh, poses, buffers, light, foil, texture("leg"), parent.rightLeg, -1.9F, 12, 0, 0, "RightFoot");
            }
            default -> { }
        }
        return true;
    }
    private BlackjackArmorRenderer() { }
}
