package com.hbm.client.entity.ufo;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import java.util.Random;
import net.minecraft.client.renderer.*;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
/** Exact BeamPronter SOLID/SPIRAL and SOLID/RANDOM calls made by RenderUFO; no altered centre, taper, endpoint or glow layers. */
public final class UfoBeamRenderer extends RenderType {
 private static final TransparencyStateShard ADDITIVE=new TransparencyStateShard("hbm_ufo_additive",()->{RenderSystem.enableBlend();RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);},()->{RenderSystem.defaultBlendFunc();RenderSystem.disableBlend();});
 private static final RenderType BEAM=com.hbm.client.render.WorldEffectMeshes.register(create("hbm_ufo_beam",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,65536,false,false,CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setTransparencyState(ADDITIVE).setCullState(NO_CULL).setWriteMaskState(COLOR_WRITE).createCompositeState(false)),"UFO+Orbus/SourceBeam");
 private UfoBeamRenderer(){super("unused",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
 public static void beam(Vec3 skeleton,PoseStack poses,MultiBufferSource buffers,boolean jitter,int rgb,int start,int segments,float size,int layers,float thickness){
  if(segments<=0)return;poses.pushPose();float yaw=(float)(Math.atan2(skeleton.x,skeleton.z)*180F/Math.PI),horizontal=(float)Math.sqrt(skeleton.x*skeleton.x+skeleton.z*skeleton.z),pitch=(float)(Math.atan2(skeleton.y,horizontal)*180F/Math.PI);
  poses.mulPose(Axis.YP.rotationDegrees(180));poses.mulPose(Axis.YP.rotationDegrees(yaw));poses.mulPose(Axis.XP.rotationDegrees(pitch-90));
  var out=buffers.getBuffer(BEAM);var random=new Random(start);double step=skeleton.length()/segments,lastX=0,lastY=0,lastZ=0;
  for(int i=0;i<=segments;i++){
   double x=size,z=0;
   for(int turn=0;turn<2;turn++){float angle=jitter?(float)Math.PI*2*random.nextFloat():turn==0?(float)Math.PI*(float)start/180F:(float)Math.PI*45F/180F*i;double nextX=x*Mth.cos(angle)+z*Mth.sin(angle);z=z*Mth.cos(angle)-x*Mth.sin(angle);x=nextX;}
   double y=step*i;
   if(i>0){float radius=thickness/layers;for(int j=1;j<=layers;j++){float r=radius*j;
    quad(poses,out,rgb,lastX+r,lastY,lastZ+r,lastX+r,lastY,lastZ-r,x+r,y,z-r,x+r,y,z+r);
    quad(poses,out,rgb,lastX-r,lastY,lastZ+r,lastX-r,lastY,lastZ-r,x-r,y,z-r,x-r,y,z+r);
    quad(poses,out,rgb,lastX+r,lastY,lastZ+r,lastX-r,lastY,lastZ+r,x-r,y,z+r,x+r,y,z+r);
    quad(poses,out,rgb,lastX+r,lastY,lastZ-r,lastX-r,lastY,lastZ-r,x-r,y,z-r,x+r,y,z-r);
   }}lastX=x;lastY=y;lastZ=z;
  }poses.popPose();
 }
 private static void quad(PoseStack poses,VertexConsumer out,int rgb,double...coordinates){for(int i=0;i<12;i+=3)out.addVertex(poses.last(),(float)coordinates[i],(float)coordinates[i+1],(float)coordinates[i+2]).setColor(0xff000000|rgb);}
}
