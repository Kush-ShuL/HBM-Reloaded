package com.hbm.weapon.legacymelee.mixin;
import com.hbm.weapon.legacymelee.BigSwordItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.world.entity.item.ItemEntity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
/** Source BigSword declines entity rotation. Modify only the final quaternion, composing with existing B92 redirect. */
@Mixin(value=ItemEntityRenderer.class,remap=false)
public abstract class BigSwordPoseMixin {
    @ModifyArgs(method="render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",at=@At(value="INVOKE",target="Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V"),require=1,allow=1)
    private void hbm$noSourceRotation(Args args,ItemEntity entity,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){if(entity.getItem().getItem() instanceof BigSwordItem)args.set(0,new Quaternionf());}
}
