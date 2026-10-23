package com.hbm.client.entity.aircraft.bomber;
import com.hbm.client.model.*;
import com.hbm.entity.aircraft.bomber.EntityBomber;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
/** Exact RenderBomber style switches, original flat Forge normals and two-sided authored geometry. */
public final class BomberRenderer extends EntityRenderer<EntityBomber> {
 private static final String[] SKINS={"dornier_1","dornier_1","dornier_2","dornier_1","dornier_4","b29_0","b29_1","b29_2","b29_3"};
 public BomberRenderer(EntityRendererProvider.Context context){super(context);}
 @Override public ResourceLocation getTextureLocation(EntityBomber entity){int style=entity.style();return ResourceLocation.parse("hbm:textures/models/"+(style>=0&&style<SKINS.length?SKINS[style]:SKINS[0])+".png");}
 @Override public boolean shouldRender(EntityBomber entity,Frustum frustum,double x,double y,double z){return true;}
 @Override public void render(EntityBomber entity,float yaw,float partial,PoseStack pose,MultiBufferSource buffers,int light){
  pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees(entity.yRotO+(entity.getYRot()-entity.yRotO)*partial-90));pose.mulPose(Axis.ZP.rotationDegrees(90));pose.mulPose(Axis.ZP.rotationDegrees(entity.xRotO+(entity.getXRot()-entity.xRotO)*partial));
  pose.mulPose(Axis.XP.rotationDegrees((float)Math.sin((entity.tickCount+partial)*.05)*10));
  int style=entity.style();String model="dornier";
  if(style>=0&&style<=4){pose.scale(5,5,5);pose.mulPose(Axis.YP.rotationDegrees(-90));}
  else if(style>=5&&style<=8){pose.scale(30F/3.1F,30F/3.1F,30F/3.1F);pose.mulPose(Axis.YP.rotationDegrees(180));model="b29";}
  var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/"+model+".obj"));
  LegacyObjRenderer.renderAll(mesh,pose,buffers.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity))),light,OverlayTexture.NO_OVERLAY,-1,LegacyObjRenderer.Convention.FORGE);pose.popPose();
 }
}
