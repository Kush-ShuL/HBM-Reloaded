package com.hbm.client.weapon.rifle762;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Vector4f;
/** MAS36's real glClipPlane0, captured at the original call's model-view transform and applied to original HFR triangles. */
final class Rifle762Clip {
 private static final class State {boolean enabled;Vector4f plane;}
 private static final ThreadLocal<State> CURRENT=ThreadLocal.withInitial(State::new);
 static void enable(){CURRENT.get().enabled=true;}
 static void disable(){CURRENT.remove();}
 static void plane(float x,float y,float z,float d){CURRENT.get().plane=new Matrix4f(LegacyGunGL.frame().pose().last().pose()).invert().transpose().transform(new Vector4f(x,y,z,d));}
 static VertexConsumer vertices(VertexConsumer delegate){var state=CURRENT.get();return state.enabled&&state.plane!=null?new Clip(delegate,new Vector4f(state.plane)):delegate;}
 private static final class Clip implements VertexConsumer {
  final VertexConsumer out;final Vector4f plane;final float[][] quad=new float[4][16];int count;float[] vertex;
  Clip(VertexConsumer out,Vector4f plane){this.out=out;this.plane=plane;}
  @Override public VertexConsumer addVertex(float x,float y,float z){vertex=quad[count++];vertex[0]=x;vertex[1]=y;vertex[2]=z;return this;}
  @Override public VertexConsumer setColor(int r,int g,int b,int a){vertex[3]=r;vertex[4]=g;vertex[5]=b;vertex[6]=a;return this;}
  @Override public VertexConsumer setUv(float u,float v){vertex[7]=u;vertex[8]=v;return this;}
  @Override public VertexConsumer setUv1(int u,int v){vertex[9]=u;vertex[10]=v;return this;}
  @Override public VertexConsumer setUv2(int u,int v){vertex[11]=u;vertex[12]=v;return this;}
  @Override public VertexConsumer setNormal(float x,float y,float z){vertex[13]=x;vertex[14]=y;vertex[15]=z;if(count==4){flush();count=0;}return this;}
  float distance(float[] p){return plane.x*p[0]+plane.y*p[1]+plane.z*p[2]+plane.w;}
  void flush(){int inside=0;for(var p:quad)if(distance(p)>=0)inside++;if(inside==0)return;if(inside==4){for(var p:quad)emit(p);return;}triangle(quad[0],quad[1],quad[2]);triangle(quad[0],quad[2],quad[3]);}
  void triangle(float[] a,float[] b,float[] c){if(a[0]==c[0]&&a[1]==c[1]&&a[2]==c[2]||b[0]==c[0]&&b[1]==c[1]&&b[2]==c[2])return;
   var input=List.of(a,b,c);var result=new ArrayList<float[]>(4);var previous=input.get(input.size()-1);float old=distance(previous);
   for(var point:input){float next=distance(point);if((old>=0)!=(next>=0)){float amount=old/(old-next);var cut=new float[16];for(int j=0;j<16;j++)cut[j]=previous[j]+amount*(point[j]-previous[j]);result.add(cut);}if(next>=0)result.add(point);previous=point;old=next;}
   for(int i=1;i+1<result.size();i++){emit(result.get(0));emit(result.get(i));emit(result.get(i+1));emit(result.get(i+1));}
  }
  void emit(float[] p){out.addVertex(p[0],p[1],p[2]).setColor(Math.round(p[3]),Math.round(p[4]),Math.round(p[5]),Math.round(p[6])).setUv(p[7],p[8]).setUv1(Math.round(p[9]),Math.round(p[10])).setUv2(Math.round(p[11]),Math.round(p[12])).setNormal(p[13],p[14],p[15]);}
 }
 private Rifle762Clip(){}
}
