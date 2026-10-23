package com.hbm.client.forcefield;
import com.mojang.blaze3d.vertex.*;import com.mojang.math.Axis;import net.minecraft.client.renderer.MultiBufferSource;import net.minecraft.world.phys.Vec3;
/** Both original RenderMachineForceField and RenderForceField sphere loops, same float angles and incremental vectors. */
public final class ForceFieldWireframe {
 public static void prototype(PoseStack poses,MultiBufferSource buffers){render(poses,buffers,8,16,10,0x0088ff);}
 public static void render(PoseStack poses,MultiBufferSource buffers,int latitude,int longitude,float radius,int rgb){
  var vertices=buffers.getBuffer(ForceFieldRenderTypes.WIREFRAME);int color=0xff000000|rgb;poses.pushPose();float around=360F/longitude,down=(float)Math.PI/latitude;
  for(int k=0;k<longitude;k++){poses.mulPose(Axis.YP.rotationDegrees(around));Vec3 vec=new Vec3(0,radius,0);for(int i=0;i<latitude;i++){point(vertices,poses,vec,color);vec=vec.xRot(down);point(vertices,poses,vec,color);}}
  poses.popPose();float aroundY=(float)Math.PI*2F/longitude;Vec3 vec=new Vec3(0,radius,0);
  for(int k=0;k<latitude;k++){vec=vec.zRot(down);for(int i=0;i<longitude;i++){point(vertices,poses,vec,color);vec=vec.yRot(aroundY);point(vertices,poses,vec,color);}}
 }
 private static void point(VertexConsumer vertices,PoseStack poses,Vec3 point,int color){vertices.addVertex(poses.last().pose(),(float)point.x,(float)point.y,(float)point.z).setColor(color);}
 private ForceFieldWireframe(){}
}
