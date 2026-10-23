package com.hbm.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

/** The original Minecraft ItemRenderer.renderItemIn2D extrusion, without item-model substitution or a shader. */
public final class LegacyItemIn2D {
 public static void render(PoseStack poses,VertexConsumer out,int overlay,int light,int argb,int width,int height,float depth){
  quad(poses,out,overlay,light,argb,new float[][]{{0,0,0,1,1},{1,0,0,0,1},{1,1,0,0,0},{0,1,0,1,0}},0,0,1);
  quad(poses,out,overlay,light,argb,new float[][]{{0,1,-depth,1,0},{1,1,-depth,0,0},{1,0,-depth,0,1},{0,0,-depth,1,1}},0,0,-1);
  for(int i=0;i<width;i++){
   float x=(float)i/width,u=1-x-.5F/width;
   quad(poses,out,overlay,light,argb,new float[][]{{x,0,-depth,u,1},{x,0,0,u,1},{x,1,0,u,0},{x,1,-depth,u,0}},-1,0,0);float right=x+1F/width;
   quad(poses,out,overlay,light,argb,new float[][]{{right,1,-depth,u,0},{right,1,0,u,0},{right,0,0,u,1},{right,0,-depth,u,1}},1,0,0);
  }
  for(int i=0;i<height;i++){
   float y=(float)i/height,v=1-y-.5F/height;
   quad(poses,out,overlay,light,argb,new float[][]{{0,y+1F/height,0,1,v},{1,y+1F/height,0,0,v},{1,y+1F/height,-depth,0,v},{0,y+1F/height,-depth,1,v}},0,1,0);
   quad(poses,out,overlay,light,argb,new float[][]{{1,y,0,0,v},{0,y,0,1,v},{0,y,-depth,1,v},{1,y,-depth,0,v}},0,-1,0);
  }
 }
 private static void quad(PoseStack poses,VertexConsumer out,int overlay,int light,int color,float[][] vertices,float nx,float ny,float nz){for(float[] vertex:vertices)out.addVertex(poses.last(),vertex[0],vertex[1],vertex[2]).setColor(color).setUv(vertex[3],vertex[4]).setOverlay(overlay).setLight(light).setNormal(poses.last(),nx,ny,nz);}
 private LegacyItemIn2D(){}
}
