package com.hbm.client.weapon.b92.mixin;
import com.hbm.weapon.b92.B92Content;import net.minecraft.client.renderer.entity.ItemEntityRenderer;import net.minecraft.world.entity.item.ItemEntity;import org.spongepowered.asm.mixin.Mixin;import org.spongepowered.asm.mixin.injection.*;
/** Source ItemRenderGunAnim declines ENTITY_ROTATION helper; native extension only exposes the bobbing switch. */
@Mixin(ItemEntityRenderer.class)
public abstract class B92DroppedSpinMixin {
 @Redirect(method="render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",at=@At(value="INVOKE",target="Lnet/minecraft/world/entity/item/ItemEntity;getSpin(F)F"))
 private float hbm$b92NoRotation(ItemEntity entity,float partial){return entity.getItem().is(B92Content.GUN.get())||net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(entity.getItem().getItem()).toString().equals("hbm:redstone_sword")?0:entity.getSpin(partial);}
}
