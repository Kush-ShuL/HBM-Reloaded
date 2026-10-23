package com.hbm.client.ashgoggles;

import com.hbm.client.armor.t51.T51ArmorRenderer;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.equipment.ashgoggles.AshGogglesContent;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/** ModelGlasses type0 draws the whole original goggles OBJ at the copied head pose. */
@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class AshGogglesClient {
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("hbm", "models/armor/goggles.obj");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("hbm", "textures/armor/goggles.png");
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> T51ArmorRenderer.register(AshGogglesContent.GOGGLES.get(), AshGogglesClient::render));
    }
    private static boolean render(PoseStack poses, MultiBufferSource buffers, int light, LivingEntity entity,
        EquipmentSlot slot, HumanoidModel<?> parent) {
        var stack = entity.getItemBySlot(slot);
        if (slot != EquipmentSlot.HEAD || !stack.is(AshGogglesContent.GOGGLES.get())) return false;
        var mesh = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), MODEL);
        poses.pushPose();
        try {
            var part = parent.head;
            poses.translate(part.x / 16F, part.y / 16F, part.z / 16F);
            if (part.zRot != 0) poses.mulPose(Axis.ZP.rotation(part.zRot));
            if (part.yRot != 0) poses.mulPose(Axis.YP.rotation(part.yRot));
            if (part.xRot != 0) poses.mulPose(Axis.XP.rotation(part.xRot));
            poses.scale(1F / 16F, 1F / 16F, 1F / 16F);
            LegacyObjRenderer.renderAll(mesh, poses, ItemRenderer.getArmorFoilBuffer(buffers,
                RenderType.armorCutoutNoCull(TEXTURE), stack.hasFoil()), light, OverlayTexture.NO_OVERLAY,
                -1, LegacyObjRenderer.Convention.HFR);
        } finally { poses.popPose(); }
        return true;
    }
    private AshGogglesClient() { }
}
