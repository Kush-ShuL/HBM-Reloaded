package com.hbm.client.meteordeco;
import java.util.*;
/** Exact default-rotation RenderBlocks face UVs from official Minecraft 1.7.10 blm, MCP SRG identified. */
public final class SourceFaces {
 public record Vertex(float x,float y,float z,float u,float v){}
 public record Face(String texture,List<Vertex> vertices,float red,float green,float blue,int light,float nx,float ny,float nz){}
 final List<Face> faces=new ArrayList<>();private final List<Vertex> pending=new ArrayList<>(4);
 private double i,j,k,l,m,n;
 /** Source field_152631_f: XPos/ZNeg reflect U about the WHOLE icon, u -> 1-u.
  * For a partial 0..3/8 face this selects 5/8..1; swapping its local UV ends is wrong.
  * Inner-wall render-call offsets change positions only, never these original bounds/UVs. */
 boolean f,nt;private float red=1,green=1,blue=1,nx,ny,nz;private int light;private String texture;
 SourceFaces(int light){this.light=light;}
 void setRenderBounds(double minX,double minY,double minZ,double maxX,double maxY,double maxZ){i=minX;j=maxX;k=minY;l=maxY;m=minZ;n=maxZ;}
 void setBrightness(int value){light=value;}
 void setColorOpaque_F(float r,float g,float b){red=r;green=g;blue=b;}
 void setNormal(float x,float y,float z){nx=x;ny=y;nz=z;}
 void startDrawingQuads(){} void draw(){}
 private void begin(String texture,float x,float y,float z){this.texture=texture;nx=x;ny=y;nz=z;}
 private void a(double x,double y,double z,double u,double v){pending.add(new Vertex((float)x,(float)y,(float)z,(float)u,(float)v));if(pending.size()==4){faces.add(new Face(texture,List.copyOf(pending),red,green,blue,light,nx,ny,nz));pending.clear();}}
 private static final class Icon{double a(double pixel){return pixel/16;}double b(double pixel){return pixel/16;}double c(){return 0;}double d(){return 1;}double e(){return 0;}double f(){return 1;}}
void renderFaceYNeg(Object var1, double var2, double var4, double var6, String texture) {
      SourceFaces var9 = this; Icon var8 = new Icon(); begin(texture,0,-1,0);


      double var10 = var8.a(this.i * 16.0);
      double var12 = var8.a(this.j * 16.0);
      double var14 = var8.b(this.m * 16.0);
      double var16 = var8.b(this.n * 16.0);
      if (this.i < 0.0 || this.j > 1.0) {
         var10 = var8.c();
         var12 = var8.d();
      }

      if (this.m < 0.0 || this.n > 1.0) {
         var14 = var8.e();
         var16 = var8.f();
      }

      double var18 = var12;
      double var20 = var10;
      double var22 = var14;
      double var24 = var16;


      double var26 = var2 + this.i;
      double var28 = var2 + this.j;
      double var30 = var4 + this.k;
      double var32 = var6 + this.m;
      double var34 = var6 + this.n;
      if (false) {
         var26 = var2 + this.j;
         var28 = var2 + this.i;
      }


         var9.a(var26, var30, var34, var20, var24);
         var9.a(var26, var30, var32, var10, var14);
         var9.a(var28, var30, var32, var18, var22);
         var9.a(var28, var30, var34, var12, var16);
      
   }
void renderFaceYPos(Object var1, double var2, double var4, double var6, String texture) {
      SourceFaces var9 = this; Icon var8 = new Icon(); begin(texture,0,1,0);


      double var10 = var8.a(this.i * 16.0);
      double var12 = var8.a(this.j * 16.0);
      double var14 = var8.b(this.m * 16.0);
      double var16 = var8.b(this.n * 16.0);
      if (this.i < 0.0 || this.j > 1.0) {
         var10 = var8.c();
         var12 = var8.d();
      }

      if (this.m < 0.0 || this.n > 1.0) {
         var14 = var8.e();
         var16 = var8.f();
      }

      double var18 = var12;
      double var20 = var10;
      double var22 = var14;
      double var24 = var16;


      double var26 = var2 + this.i;
      double var28 = var2 + this.j;
      double var30 = var4 + this.l;
      double var32 = var6 + this.m;
      double var34 = var6 + this.n;
      if (false) {
         var26 = var2 + this.j;
         var28 = var2 + this.i;
      }


         var9.a(var28, var30, var34, var12, var16);
         var9.a(var28, var30, var32, var18, var22);
         var9.a(var26, var30, var32, var10, var14);
         var9.a(var26, var30, var34, var20, var24);
      
   }
void renderFaceZNeg(Object var1, double var2, double var4, double var6, String texture) {
      SourceFaces var9 = this; Icon var8 = new Icon(); begin(texture,0,0,-1);


      double var10 = var8.a(this.i * 16.0);
      double var12 = var8.a(this.j * 16.0);
      if (this.f) {
         var12 = var8.a((1.0 - this.i) * 16.0);
         var10 = var8.a((1.0 - this.j) * 16.0);
      }

      if(nt){var10=f?this.j:1-this.j;var12=f?this.i:1-this.i;}
      double var14 = var8.b(16.0 - this.l * 16.0);
      double var16 = var8.b(16.0 - this.k * 16.0);
      if (false) {
         double var18 = var10;
         var10 = var12;
         var12 = var18;
      }

      if (this.i < 0.0 || this.j > 1.0) {
         var10 = var8.c();
         var12 = var8.d();
      }

      if (this.k < 0.0 || this.l > 1.0) {
         var14 = var8.e();
         var16 = var8.f();
      }

      double var40 = var12;
      double var20 = var10;
      double var22 = var14;
      double var24 = var16;


      double var26 = var2 + this.i;
      double var28 = var2 + this.j;
      double var30 = var4 + this.k;
      double var32 = var4 + this.l;
      double var34 = var6 + this.m;
      if (false) {
         var26 = var2 + this.j;
         var28 = var2 + this.i;
      }


         var9.a(var26, var32, var34, var40, var22);
         var9.a(var28, var32, var34, var10, var14);
         var9.a(var28, var30, var34, var20, var24);
         var9.a(var26, var30, var34, var12, var16);
      
   }
void renderFaceZPos(Object var1, double var2, double var4, double var6, String texture) {
      SourceFaces var9 = this; Icon var8 = new Icon(); begin(texture,0,0,1);


      double var10 = var8.a(this.i * 16.0);
      double var12 = var8.a(this.j * 16.0);
      double var14 = var8.b(16.0 - this.l * 16.0);
      double var16 = var8.b(16.0 - this.k * 16.0);
      if (false) {
         double var18 = var10;
         var10 = var12;
         var12 = var18;
      }

      if (this.i < 0.0 || this.j > 1.0) {
         var10 = var8.c();
         var12 = var8.d();
      }

      if (this.k < 0.0 || this.l > 1.0) {
         var14 = var8.e();
         var16 = var8.f();
      }

      double var40 = var12;
      double var20 = var10;
      double var22 = var14;
      double var24 = var16;


      double var26 = var2 + this.i;
      double var28 = var2 + this.j;
      double var30 = var4 + this.k;
      double var32 = var4 + this.l;
      double var34 = var6 + this.n;
      if (false) {
         var26 = var2 + this.j;
         var28 = var2 + this.i;
      }


         var9.a(var26, var32, var34, var10, var14);
         var9.a(var26, var30, var34, var20, var24);
         var9.a(var28, var30, var34, var12, var16);
         var9.a(var28, var32, var34, var40, var22);
      
   }
void renderFaceXNeg(Object var1, double var2, double var4, double var6, String texture) {
      SourceFaces var9 = this; Icon var8 = new Icon(); begin(texture,-1,0,0);


      double var10 = var8.a(this.m * 16.0);
      double var12 = var8.a(this.n * 16.0);
      double var14 = var8.b(16.0 - this.l * 16.0);
      double var16 = var8.b(16.0 - this.k * 16.0);
      if (false) {
         double var18 = var10;
         var10 = var12;
         var12 = var18;
      }

      if (this.m < 0.0 || this.n > 1.0) {
         var10 = var8.c();
         var12 = var8.d();
      }

      if (this.k < 0.0 || this.l > 1.0) {
         var14 = var8.e();
         var16 = var8.f();
      }

      double var40 = var12;
      double var20 = var10;
      double var22 = var14;
      double var24 = var16;


      double var26 = var2 + this.i;
      double var28 = var4 + this.k;
      double var30 = var4 + this.l;
      double var32 = var6 + this.m;
      double var34 = var6 + this.n;
      if (false) {
         var32 = var6 + this.n;
         var34 = var6 + this.m;
      }


         var9.a(var26, var30, var34, var40, var22);
         var9.a(var26, var30, var32, var10, var14);
         var9.a(var26, var28, var32, var20, var24);
         var9.a(var26, var28, var34, var12, var16);
      
   }
void renderFaceXPos(Object var1, double var2, double var4, double var6, String texture) {
      SourceFaces var9 = this; Icon var8 = new Icon(); begin(texture,1,0,0);


      double var10 = var8.a(this.m * 16.0);
      double var12 = var8.a(this.n * 16.0);
      if (this.f) {
         var12 = var8.a((1.0 - this.m) * 16.0);
         var10 = var8.a((1.0 - this.n) * 16.0);
      }

      if(nt){var10=f?this.n:1-this.n;var12=f?this.m:1-this.m;}
      double var14 = var8.b(16.0 - this.l * 16.0);
      double var16 = var8.b(16.0 - this.k * 16.0);
      if (false) {
         double var18 = var10;
         var10 = var12;
         var12 = var18;
      }

      if (this.m < 0.0 || this.n > 1.0) {
         var10 = var8.c();
         var12 = var8.d();
      }

      if (this.k < 0.0 || this.l > 1.0) {
         var14 = var8.e();
         var16 = var8.f();
      }

      double var40 = var12;
      double var20 = var10;
      double var22 = var14;
      double var24 = var16;


      double var26 = var2 + this.j;
      double var28 = var4 + this.k;
      double var30 = var4 + this.l;
      double var32 = var6 + this.m;
      double var34 = var6 + this.n;
      if (false) {
         var32 = var6 + this.n;
         var34 = var6 + this.m;
      }


         var9.a(var26, var28, var34, var20, var24);
         var9.a(var26, var28, var32, var12, var16);
         var9.a(var26, var30, var32, var40, var22);
         var9.a(var26, var30, var34, var10, var14);
      
   }

 private String override;
 void setOverrideBlockTexture(String texture){override=texture;}void clearOverrideBlockTexture(){override=null;}
 void renderStandardBlock(SourceGeometry.Context block,int x,int y,int z){String texture=override==null?block.getIcon(0,block.metadata()):override;renderFaceYNeg(block,x,y,z,texture);renderFaceYPos(block,x,y,z,texture);renderFaceZNeg(block,x,y,z,texture);renderFaceZPos(block,x,y,z,texture);renderFaceXNeg(block,x,y,z,texture);renderFaceXPos(block,x,y,z,texture);}
}
