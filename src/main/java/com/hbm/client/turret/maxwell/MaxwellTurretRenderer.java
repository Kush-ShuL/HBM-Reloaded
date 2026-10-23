package com.hbm.client.turret.maxwell;
import com.hbm.client.model.*;
import com.hbm.client.reactor.dfc.components.DfcComponentBeams;
import com.hbm.client.turret.rotary.RotaryTurretRenderer;
import com.hbm.machine.turret.maxwell.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
/** Original separate Chekhov Base / Howard CIWS Carriage / Microwave meshes and eight actual SPIRAL/SOLID beams. */
public final class MaxwellTurretRenderer implements BlockEntityRenderer<MaxwellTurretEntity> {
 static final ResourceLocation BASE=MaxwellTurretContent.id("models/turrets/turret_chekhov.obj"),CARRIAGE=MaxwellTurretContent.id("models/turrets/turret_howard.obj"),MODEL=MaxwellTurretContent.id("models/turrets/turret_microwave.obj");
 public MaxwellTurretRenderer(BlockEntityRendererProvider.Context context){}
 static WavefrontMesh mesh(ResourceLocation id){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),id);}
 static void body(PoseStack pose,MultiBufferSource buffers,int light,int overlay){
  RotaryTurretRenderer.part(mesh(BASE),pose,buffers,light,overlay,"base","Base");
  RotaryTurretRenderer.part(mesh(CARRIAGE),pose,buffers,light,overlay,"carriage_ciws","Carriage");
  RotaryTurretRenderer.part(mesh(MODEL),pose,buffers,light,overlay,"maxwell","Microwave");
 }
 @Override public void render(MaxwellTurretEntity turret,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
  var base=mesh(BASE);var offset=turret.horizontalOffset();
  LegacyContactShadows.render(turret,RotaryTurretRenderer.shadow(base),new org.joml.Matrix4f().translation((float)offset.x,0,(float)offset.z),pose,buffers);
  pose.pushPose();try{
   pose.translate(offset.x,0,offset.z);RotaryTurretRenderer.connectors(turret,base,pose,buffers,light,overlay);
   RotaryTurretRenderer.part(base,pose,buffers,light,overlay,"base","Base");
   pose.mulPose(Axis.YP.rotationDegrees((float)-Math.toDegrees(Mth.lerp(partial,turret.lastRotationYaw,turret.rotationYaw))-90));
   RotaryTurretRenderer.part(mesh(CARRIAGE),pose,buffers,light,overlay,"carriage_ciws","Carriage");
   pose.translate(0,1.5,0);pose.mulPose(Axis.ZP.rotationDegrees((float)Math.toDegrees(Mth.lerp(partial,turret.lastRotationPitch,turret.rotationPitch))));pose.translate(0,-1.5,0);
   RotaryTurretRenderer.part(mesh(MODEL),pose,buffers,light,overlay,"maxwell","Microwave");
   if(turret.beam>0){
    pose.pushPose();try{
     pose.translate(turret.barrelLength(),2,0);pose.mulPose(Axis.YP.rotationDegrees(90));double length=turret.lastDist-turret.barrelLength();
     for(int i=0;i<8;i++)DfcComponentBeams.draw(pose,buffers,length,false,true,0x2020ff,0x2020ff,(int)((turret.getLevel().getGameTime()+partial)*-50+i*45)%360,(int)(turret.lastDist+1),.375F,2,.05F);
    }finally{pose.popPose();}
   }
  }finally{pose.popPose();}
 }
 @Override public boolean shouldRenderOffScreen(MaxwellTurretEntity turret){return true;}
 @Override public int getViewDistance(){return 256;}
 @Override public AABB getRenderBoundingBox(MaxwellTurretEntity turret){return new AABB(Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY);}
}
