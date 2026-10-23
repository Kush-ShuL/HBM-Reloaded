package com.hbm.world.structure.templates.radio.client;
import com.hbm.client.model.WavefrontMesh;
import com.hbm.client.model.legacy.generated.ModelBroadcaster;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
/** Captures the actual four source Java-model boxes, with their real render matrices and vertex order. */
final class RadioReceiverMesh implements VertexConsumer {
    private final List<WavefrontMesh.Position> points=new ArrayList<>();
    static WavefrontMesh capture(ModelBroadcaster model){var capture=new RadioReceiverMesh();model.renderToBuffer(new PoseStack(),capture,0,0,-1,.0625F);var faces=new ArrayList<WavefrontMesh.Face>();var triangles=new ArrayList<WavefrontMesh.Triangle>();if(capture.points.size()%4!=0)throw new IllegalStateException("Incomplete source radio model quads");for(int i=0;i<capture.points.size();i+=4){int face=faces.size();var corners=new ArrayList<WavefrontMesh.Corner>();for(int c=0;c<4;c++)corners.add(new WavefrontMesh.Corner(i+c,-1,-1));faces.add(new WavefrontMesh.Face(corners,"radio",List.of(),"radio",null,null,new WavefrontMesh.Normal(0,0,0),-1));triangles.add(new WavefrontMesh.Triangle(face,0,1,2));triangles.add(new WavefrontMesh.Triangle(face,0,2,3));}return new WavefrontMesh(capture.points,List.of(),List.of(),faces,triangles,List.of(),List.of(),List.of());}
    @Override public VertexConsumer addVertex(float x,float y,float z){points.add(new WavefrontMesh.Position(x,y,z,1));return this;}
    @Override public VertexConsumer setColor(int r,int g,int b,int a){return this;}
    @Override public VertexConsumer setUv(float u,float v){return this;}
    @Override public VertexConsumer setUv1(int u,int v){return this;}
    @Override public VertexConsumer setUv2(int u,int v){return this;}
    @Override public VertexConsumer setNormal(float x,float y,float z){return this;}
}
