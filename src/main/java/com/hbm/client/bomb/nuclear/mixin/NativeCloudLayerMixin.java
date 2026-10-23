package com.hbm.client.bomb.nuclear.mixin;
import com.hbm.client.bomb.nuclear.NuclearTransparencyLayers;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4f;
@Mixin(LevelRenderer.class)
abstract class NativeCloudLayerMixin {
 /** Wrap the call, so a Sodium/Iris cancellation inside renderClouds cannot skip capture cleanup. */
 @WrapOperation(method="renderLevel",at=@At(value="INVOKE",target="Lnet/minecraft/client/renderer/LevelRenderer;renderClouds(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FDDD)V"))
 private void hbm$cloudScope(LevelRenderer renderer,PoseStack pose,Matrix4f view,Matrix4f projection,float partial,double x,double y,double z,Operation<Void> original){
  boolean completed=false;
  try{NuclearTransparencyLayers.beginVanillaClouds();original.call(renderer,pose,view,projection,partial,x,y,z);completed=true;}
  finally{NuclearTransparencyLayers.endVanillaClouds(completed);}
 }
}
