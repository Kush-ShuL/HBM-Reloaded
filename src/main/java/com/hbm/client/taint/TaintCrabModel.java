package com.hbm.client.taint;
import com.hbm.client.model.*;
import com.hbm.world.taint.TaintCrab;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
/** Original ModelTaintCrab Body/Legs1/Legs2 rotations and ForgeOBJ UV/normals. */
public final class TaintCrabModel extends EntityModel<TaintCrab> {
 private float swing,amount;
 public TaintCrabModel(){super(RenderType::entityCutout);}
 @Override public void setupAnim(TaintCrab entity,float swing,float amount,float age,float yaw,float pitch){this.swing=swing;this.amount=amount;}
 @Override public void renderToBuffer(PoseStack pose,VertexConsumer out,int light,int overlay,int color){
  var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/mobs/taintcrab.obj"));
  pose.pushPose();pose.translate(0,-.0068125,0);pose.mulPose(Axis.YN.rotationDegrees(90));pose.mulPose(Axis.ZP.rotationDegrees(180));pose.translate(0,-1.5F,0);
  float rotation=-(Mth.cos(swing*.6662F*2)*.4F)*amount*57.3F;
  LegacyObjRenderer.renderOnly(mesh,pose,out,light,overlay,color,LegacyObjRenderer.Convention.FORGE,"Body");
  pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees(rotation));LegacyObjRenderer.renderOnly(mesh,pose,out,light,overlay,color,LegacyObjRenderer.Convention.FORGE,"Legs1");pose.popPose();
  pose.pushPose();pose.mulPose(Axis.YN.rotationDegrees(rotation));LegacyObjRenderer.renderOnly(mesh,pose,out,light,overlay,color,LegacyObjRenderer.Convention.FORGE,"Legs2");pose.popPose();pose.popPose();
 }
}
