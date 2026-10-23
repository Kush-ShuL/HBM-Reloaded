package com.hbm.client.boostmurk.mixin;

import com.hbm.client.boostmurk.MurkWingRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Only the three new standalone source chest items on actual nonplayer bipeds; no second player layer. */
@Mixin(value=HumanoidArmorLayer.class,remap=false)
public abstract class BoostMurkArmorLayerMixin {
    @Inject(method="renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V",at=@At("HEAD"),cancellable=true)
    private void hbm$boostMurk(PoseStack poses,MultiBufferSource buffers,LivingEntity entity,EquipmentSlot slot,int light,HumanoidModel<?> armor,float swing,float amount,float partial,float age,float yaw,float pitch,CallbackInfo ci){
        var parent=((HumanoidArmorLayer<?,?,?>)(Object)this).getParentModel();
        if(MurkWingRenderer.renderNonPlayer(poses,buffers,light,entity,slot,parent,swing,amount,age,yaw,pitch))ci.cancel();
    }
}
