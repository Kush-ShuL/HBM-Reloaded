package com.hbm.client.equipment.npcheadgear.mixin;

import com.hbm.client.equipment.npcheadgear.NpcHeadgearArmorRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=HumanoidArmorLayer.class, remap=false)
public abstract class HeadgearArmorMixin {
    @Inject(method="renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V", at=@At("HEAD"), cancellable=true)
    private void hbm$headgear(PoseStack poses, MultiBufferSource buffers, LivingEntity living, EquipmentSlot slot,
            int light, HumanoidModel<?> armor, float swing, float amount, float partial, float age, float yaw,
            float pitch, CallbackInfo ci) {
        var parent = ((HumanoidArmorLayer<?, ?, ?>) (Object) this).getParentModel();
        if (NpcHeadgearArmorRenderer.render(poses, buffers, light, living, slot, parent)) ci.cancel();
    }
}
