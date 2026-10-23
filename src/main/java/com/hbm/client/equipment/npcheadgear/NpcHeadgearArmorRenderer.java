package com.hbm.client.equipment.npcheadgear;

import com.hbm.client.model.*;
import com.hbm.equipment.npcheadgear.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;

/** Exact authored ModelHat/ModelNo9 head OBJ, original parts/textures and fullbright untextured flame. */
public final class NpcHeadgearArmorRenderer {
    private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("hbm", path); }
    public static boolean render(PoseStack poses, MultiBufferSource buffers, int light, LivingEntity entity,
            EquipmentSlot slot, HumanoidModel<?> parent) {
        if (slot != EquipmentSlot.HEAD) return false;
        var stack = entity.getItemBySlot(slot);
        boolean no9 = stack.getItem() instanceof MiningHelmetItem, hat = stack.getItem() instanceof FabulousHatItem;
        if (!no9 && !hat) return false;
        var model = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), id("models/armor/" + (no9 ? "no9" : "hat") + ".obj"));
        poses.pushPose();
        try {
            var part = parent.head;
            poses.translate(part.x / 16F, part.y / 16F, part.z / 16F);
            if (part.zRot != 0) poses.mulPose(Axis.ZP.rotation(part.zRot));
            if (part.yRot != 0) poses.mulPose(Axis.YP.rotation(part.yRot));
            if (part.xRot != 0) poses.mulPose(Axis.XP.rotation(part.xRot));
            poses.scale(1F / 16F, 1F / 16F, 1F / 16F);
            if (hat) {
                LegacyObjRenderer.renderAll(model, poses, ItemRenderer.getArmorFoilBuffer(buffers,
                        RenderType.armorCutoutNoCull(id("textures/armor/hat.png")), stack.hasFoil()), light,
                        OverlayTexture.NO_OVERLAY, -1, LegacyObjRenderer.Convention.HFR_FLAT);
            } else {
                draw(model, poses, buffers, light, stack.hasFoil(), "no9", "Helmet");
                draw(model, poses, buffers, light, stack.hasFoil(), "no9_insignia", "Insignia");
                if (entity instanceof Player && MiningHelmetItem.isOn(stack))
                    LegacyObjRenderer.renderOnly(model, poses, buffers.getBuffer(No9RenderType.FLAME),
                            LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0xffffffcc,
                            LegacyObjRenderer.Convention.HFR, "Flame");
            }
        } finally { poses.popPose(); }
        return true;
    }
    private static void draw(WavefrontMesh model, PoseStack poses, MultiBufferSource buffers, int light,
            boolean foil, String texture, String part) {
        LegacyObjRenderer.renderOnly(model, poses, ItemRenderer.getArmorFoilBuffer(buffers,
                RenderType.armorCutoutNoCull(id("textures/armor/" + texture + ".png")), foil), light,
                OverlayTexture.NO_OVERLAY, -1, LegacyObjRenderer.Convention.HFR, part);
    }
    private NpcHeadgearArmorRenderer() {}
}
