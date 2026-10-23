package com.hbm.client.armor.envsuit;

import com.hbm.armor.envsuit.ArmorEnvsuit;
import com.hbm.armor.envsuit.EnvsuitContent;
import com.hbm.client.armor.t51.T51ArmorRenderer;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.client.model.WavefrontMesh;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;

/** Exact ModelArmorEnvsuit nine authored groups, source pivots and separate translucent helmet/untextured lamps. */
public final class EnvsuitArmorRenderer {
    public static final ResourceLocation MODEL = EnvsuitContent.id("models/armor/envsuit.obj");
    private static final ResourceLocation WHITE = EnvsuitContent.id("textures/misc/envsuit_untextured.png");
    public static ResourceLocation texture(String name) { return EnvsuitContent.id("textures/armor/envsuit_" + name + ".png"); }
    public static boolean render(PoseStack poses, MultiBufferSource buffers, int light, LivingEntity entity, EquipmentSlot slot, HumanoidModel<?> parent) {
        var stack = entity.getItemBySlot(slot);
        if (!(stack.getItem() instanceof ArmorEnvsuit armor) || armor.getEquipmentSlot() != slot) return false;
        var mesh = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), MODEL);
        boolean foil = stack.hasFoil();
        switch (slot) {
            case HEAD -> {
                poses.pushPose();
                try {
                    var head = parent.head;
                    poses.translate(head.x / 16F, head.y / 16F, head.z / 16F);
                    if (head.zRot != 0) poses.mulPose(Axis.ZP.rotation(head.zRot));
                    if (head.yRot != 0) poses.mulPose(Axis.YP.rotation(head.yRot));
                    if (head.xRot != 0) poses.mulPose(Axis.XP.rotation(head.xRot));
                    poses.scale(1F / 16, 1F / 16, 1F / 16);
                    LegacyObjRenderer.renderOnly(mesh, poses,
                        ItemRenderer.getArmorFoilBuffer(buffers, RenderType.entityTranslucent(texture("helmet")), foil),
                        light, OverlayTexture.NO_OVERLAY, -1, LegacyObjRenderer.Convention.HFR, "Helmet");
                    lamps(mesh, poses, buffers, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
                } finally { poses.popPose(); }
            }
            case CHEST -> {
                T51ArmorRenderer.part(mesh, poses, buffers, light, foil, texture("chest"), parent.body, 0, 0, 0, 0, "Chest");
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
    public static void lamps(WavefrontMesh mesh, PoseStack poses, MultiBufferSource buffers, int light, int overlay) {
        // Source disables GL_LIGHTING and GL_TEXTURE_2D: a neutral white texel preserves the actual yellow-white vertex color.
        // NeoForge's unlit shader retains its lightmap; worn source lamps force240 while item source keeps caller lightmap.
        LegacyObjRenderer.renderOnly(mesh, poses, buffers.getBuffer(NeoForgeRenderTypes.getUnlitTranslucent(WHITE, false)),
            light, overlay, 0xffffffcc, LegacyObjRenderer.Convention.HFR, "Lamps");
    }
    private EnvsuitArmorRenderer() { }
}
