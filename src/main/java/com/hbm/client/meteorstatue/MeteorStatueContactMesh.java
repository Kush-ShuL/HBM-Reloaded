package com.hbm.client.meteorstatue;
import com.hbm.client.model.WavefrontMesh;
import com.hbm.client.model.legacy.generated.*;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/** Captures actual adapted Java-model and actual watch-model vertices; never derives shadows from collision boxes. */
final class MeteorStatueContactMesh {
 private BakedModel lastWatch;private WavefrontMesh mesh;
 WavefrontMesh get(ModelStatue statue,ModelGun gun,ItemStack watch,Level level){var renderer=Minecraft.getInstance().getItemRenderer();BakedModel model=renderer.getModel(watch,level,null,0);if(mesh!=null&&lastWatch==model)return mesh;Capture capture=new Capture();PoseStack pose=new PoseStack();statue.renderToBuffer(pose,capture,LightTexture.FULL_BRIGHT,0,-1,.0625F);MeteorStatueTransforms.toWatch(pose);pose.pushPose();MeteorStatueTransforms.watchItem(pose);renderer.renderStatic(watch,ItemDisplayContext.FIXED,LightTexture.FULL_BRIGHT,0,pose,type->capture,level,0);pose.popPose();MeteorStatueTransforms.toGun(pose);gun.renderToBuffer(pose,capture,LightTexture.FULL_BRIGHT,0,-1,.0625F);mesh=capture.finish();lastWatch=model;return mesh;}
 private static final class Capture implements VertexConsumer {
  private final List<WavefrontMesh.Position> points=new ArrayList<>();
  @Override public VertexConsumer addVertex(float x,float y,float z){points.add(new WavefrontMesh.Position(x,y,z,1));return this;}
  @Override public VertexConsumer setColor(int r,int g,int b,int a){return this;}
  @Override public VertexConsumer setUv(float u,float v){return this;}
  @Override public VertexConsumer setUv1(int u,int v){return this;}
  @Override public VertexConsumer setUv2(int u,int v){return this;}
  @Override public VertexConsumer setNormal(float x,float y,float z){return this;}
  WavefrontMesh finish(){if(points.size()%4!=0)throw new IllegalStateException("Legacy statue/watch render did not emit complete quads");List<WavefrontMesh.Face> faces=new ArrayList<>();List<WavefrontMesh.Triangle> triangles=new ArrayList<>();for(int i=0;i<points.size();i+=4){int face=faces.size();List<WavefrontMesh.Corner> corners=new ArrayList<>();for(int c=0;c<4;c++)corners.add(new WavefrontMesh.Corner(i+c,-1,-1));faces.add(new WavefrontMesh.Face(corners,"statue",List.of(),"statue",null,null,new WavefrontMesh.Normal(0,0,0),-1));triangles.add(new WavefrontMesh.Triangle(face,0,1,2));triangles.add(new WavefrontMesh.Triangle(face,0,2,3));}return new WavefrontMesh(points,List.of(),List.of(),faces,triangles,List.of(),List.of(),List.of());}
 }
}
