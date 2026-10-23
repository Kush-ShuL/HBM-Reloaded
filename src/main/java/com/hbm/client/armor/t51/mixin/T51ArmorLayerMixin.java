package com.hbm.client.armor.t51.mixin;

import com.hbm.client.armor.t51.T51ArmorRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Scope is only actual ArmorT51, retaining all other vanilla/custom armor rendering. */
@Mixin(value=HumanoidArmorLayer.class,remap=false)
public abstract class T51ArmorLayerMixin {
    @Inject(method="renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V",at=@At("HEAD"),cancellable=true)
    private void hbm$t51(PoseStack poses,MultiBufferSource buffers,LivingEntity entity,EquipmentSlot slot,int light,HumanoidModel<?> armor,float swing,float amount,float partial,float age,float yaw,float pitch,CallbackInfo ci){
        var parent=((HumanoidArmorLayer<?,?,?>)(Object)this).getParentModel();
        if(T51ArmorRenderer.render(poses,buffers,light,entity,slot,parent))ci.cancel();
    }
}
