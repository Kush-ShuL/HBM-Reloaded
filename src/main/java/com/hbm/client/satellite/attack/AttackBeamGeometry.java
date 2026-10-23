package com.hbm.client.satellite.attack;
import com.hbm.bomb.nuclear.LegacyVector;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.MultiBufferSource;
/** Literal RenderDeathBlast/RenderOrbitalLaser fans, including rotateAroundY(45 radians) and unrestarted inner vector. */
final class AttackBeamGeometry {
 static void render(PoseStack pose,MultiBufferSource buffers,int outer,int inner){var vector=LegacyVector.createVectorHelper(.5,0,0);var out=buffers.getBuffer(AttackRenderTypes.BEAM);ring(pose,out,vector,1,outer);ring(pose,out,vector,.5,inner);}
 private static void ring(PoseStack pose,VertexConsumer out,LegacyVector vector,double radius,int color){for(int i=0;i<8;i++){vertex(pose,out,vector,radius,250,color);vertex(pose,out,vector,radius,0,color);vector.rotateAroundY(45);vertex(pose,out,vector,radius,0,color);vertex(pose,out,vector,radius,250,color);}}
 private static void vertex(PoseStack pose,VertexConsumer out,LegacyVector v,double scale,float y,int color){out.addVertex(pose.last(),(float)(v.xCoord*scale),y,(float)(v.zCoord*scale)).setColor(color);}
 private AttackBeamGeometry(){}
}
