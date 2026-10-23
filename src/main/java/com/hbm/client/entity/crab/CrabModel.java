package com.hbm.client.entity.crab;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import com.hbm.entity.mob.crab.*;
import net.minecraft.util.Mth;
/** Source20-box crab and original Tesla OBJ Body/Front/Back counter-rotation; no vanilla replacement model. */
public final class CrabModel extends EntityModel<CyberCrab>{
 private final ModelCrab legacy=new ModelCrab();private final boolean tesla;private CyberCrab entity;private float swing,amount,age,yaw,pitch;
 public CrabModel(boolean tesla){super(RenderType::entityCutout);this.tesla=tesla;}
 @Override public void setupAnim(CyberCrab entity,float swing,float amount,float age,float yaw,float pitch){this.entity=entity;this.swing=swing;this.amount=amount;this.age=age;this.yaw=yaw;this.pitch=pitch;if(!tesla)legacy.setRotationAngles(swing,amount,age,yaw,pitch,.0625F,entity);}
 @Override public void renderToBuffer(PoseStack poses,VertexConsumer out,int light,int overlay,int color){poses.pushPose();poses.translate(0,-.0068125,0);if(!tesla)legacy.renderToBuffer(poses,out,light,overlay,color,entity,swing,amount,age,yaw,pitch,.0625F);else{poses.mulPose(Axis.ZP.rotationDegrees(180));poses.translate(0,-1.5,0);float rotation=-(Mth.cos(swing*.6662F*2)*.4F)*amount*57.3F;var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/mobs/teslacrab.obj"));LegacyObjRenderer.renderOnly(mesh,poses,out,light,overlay,color,LegacyObjRenderer.Convention.FORGE,"Body");poses.pushPose();poses.mulPose(Axis.YP.rotationDegrees(rotation));LegacyObjRenderer.renderOnly(mesh,poses,out,light,overlay,color,LegacyObjRenderer.Convention.FORGE,"Front");poses.popPose();poses.pushPose();poses.mulPose(Axis.YN.rotationDegrees(rotation));LegacyObjRenderer.renderOnly(mesh,poses,out,light,overlay,color,LegacyObjRenderer.Convention.FORGE,"Back");poses.popPose();}poses.popPose();}
}
