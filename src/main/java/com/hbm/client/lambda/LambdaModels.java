package com.hbm.client.lambda;

import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/** Source smooth HFR parts and its three captured glClipPlane equations, interpolating actual authored UV/normals. */
public final class LambdaModels {
    public static final ResourceLocation PAD=ResourceLocation.parse("hbm:textures/models/machines/launchpad_lambda.png"),ROCKET=ResourceLocation.parse("hbm:textures/models/lambda_rocket.png");
    public static WavefrontMesh pad(){return mesh("models/machines/launchpad_lambda.obj");}public static WavefrontMesh rocket(){return mesh("models/lambda_rocket.obj");}
    private static WavefrontMesh mesh(String path){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.fromNamespaceAndPath("hbm",path));}
    public static void rocket(PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(rocket(),pose,buffers.getBuffer(RenderType.entityCutout(ROCKET)),light,overlay,0xFFFFFFFF,LegacyObjRenderer.Convention.HFR);}
    public static void part(String part,PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderOnly(pad(),pose,buffers.getBuffer(RenderType.entityCutout(PAD)),light,overlay,0xFFFFFFFF,LegacyObjRenderer.Convention.HFR,part);}
    public static void inventory(PoseStack pose,MultiBufferSource buffers,int light,int overlay){for(var name:new String[]{"Silo","DoorLeft","DoorRight"})part(name,pose,buffers,light,overlay);pose.pushPose();pose.translate(0,2,0);for(var name:new String[]{"Erector","Rotor","PivotLower1","ClampLower1","PivotLower2","ClampLower2","PivotUpper1","ClampUpper1","PivotUpper2","ClampUpper2"})part(name,pose,buffers,light,overlay);pose.popPose();}
    private record V(Vector3f p,float u,float v,Vector3f n){V mix(V b,float t){return new V(new Vector3f(p).lerp(b.p,t),u+(b.u-u)*t,v+(b.v-v)*t,new Vector3f(n).lerp(b.n,t));}}
    /** Relative contains only source animation rotations/translations; root retains camera/world orientation. */
    public static void clipped(WavefrontMesh mesh,String part,ResourceLocation texture,Matrix4f relative,Vector4f plane,PoseStack root,MultiBufferSource buffers,int light,int overlay){
        var normal=new Matrix3f(relative);var out=buffers.getBuffer(RenderType.entityCutout(texture));
        for(var triangle:mesh.triangles()){
            var face=mesh.faces().get(triangle.face());if(part!=null&&!face.legacyPart().equalsIgnoreCase(part))continue;
            var polygon=new ArrayList<V>(3);for(int index:new int[]{triangle.first(),triangle.second(),triangle.third()}){var c=face.corners().get(index);var p=mesh.positions().get(c.position());var uv=c.texture()<0?new WavefrontMesh.TexCoord(0,0,0):mesh.textureCoordinates().get(c.texture());var n=c.normal()<0?face.normal():mesh.normals().get(c.normal());polygon.add(new V(relative.transformPosition(new Vector3f(p.x(),p.y(),p.z())),uv.u(),1-uv.v(),normal.transform(new Vector3f(n.x(),n.y(),n.z()))));}
            var clipped=new ArrayList<V>(4);for(int i=0;i<polygon.size();i++){var a=polygon.get(i);var b=polygon.get((i+1)%polygon.size());float da=distance(a.p,plane),db=distance(b.p,plane);if(da>=0)clipped.add(a);if((da>=0)!=(db>=0))clipped.add(a.mix(b,da/(da-db)));}
            for(int i=1;i+1<clipped.size();i++){emit(out,root,clipped.getFirst(),light,overlay);emit(out,root,clipped.get(i),light,overlay);emit(out,root,clipped.get(i+1),light,overlay);emit(out,root,clipped.get(i+1),light,overlay);}
        }
    }
    private static float distance(Vector3f p,Vector4f plane){return p.x*plane.x+p.y*plane.y+p.z*plane.z+plane.w;}
    private static void emit(VertexConsumer out,PoseStack pose,V v,int light,int overlay){out.addVertex(pose.last(),v.p.x,v.p.y,v.p.z).setColor(0xFFFFFFFF).setUv(v.u,v.v).setOverlay(overlay).setLight(light).setNormal(pose.last(),v.n.x,v.n.y,v.n.z);}
    private LambdaModels(){}
}
