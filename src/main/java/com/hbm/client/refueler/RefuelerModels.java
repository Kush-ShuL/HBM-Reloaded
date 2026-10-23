package com.hbm.client.refueler;

import com.hbm.client.model.*;
import com.hbm.client.model.WavefrontMesh.*;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;

/** Source unchanged HFR parts; CPU clipping exactly replaces GL_CLIP_PLANE0, without caps or retained per-frame meshes. */
final class RefuelerModels {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/blocks/refueler.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/refueler.png");
    private static WavefrontMesh mesh(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    static void body(PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderOnly(mesh(),poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR,"Fueler");}
    private record Vertex(float x,float y,float z,float nx,float ny,float nz){Vertex lerp(Vertex other,float t){return new Vertex(x+(other.x-x)*t,y+(other.y-y)*t,z+(other.z-z)*t,nx+(other.nx-nx)*t,ny+(other.ny-ny)*t,nz+(other.nz-nz)*t);}}
    private static Vertex vertex(WavefrontMesh mesh,Face face,int index,float offset){Corner c=face.corners().get(index);Position p=mesh.positions().get(c.position());Normal n=c.normal()>=0?mesh.normals().get(c.normal()):face.normal();return new Vertex(p.x(),p.y()+offset,p.z(),n.x(),n.y(),n.z());}
    static void fluid(PoseStack poses,MultiBufferSource buffers,int light,int overlay,int color,double fill){
        var mesh=mesh();var output=buffers.getBuffer(RefuelerRenderTypes.FLUID);float offset=(float)((1-fill)*-.625);int argb=0xbf000000|(color&0xffffff);
        for(Triangle triangle:mesh.triangles()){
            Face face=mesh.faces().get(triangle.face());if(!face.legacyPart().equalsIgnoreCase("Fluid"))continue;
            List<Vertex> polygon=List.of(vertex(mesh,face,triangle.first(),offset),vertex(mesh,face,triangle.second(),offset),vertex(mesh,face,triangle.third(),offset));
            var clipped=new ArrayList<Vertex>(4);Vertex before=polygon.getLast();boolean insideBefore=before.y>=.125F;
            for(Vertex after:polygon){boolean insideAfter=after.y>=.125F;if(insideBefore!=insideAfter)clipped.add(before.lerp(after,(.125F-before.y)/(after.y-before.y)));if(insideAfter)clipped.add(after);before=after;insideBefore=insideAfter;}
            for(int i=1;i+1<clipped.size();i++){emit(clipped.getFirst(),poses,output,argb,light,overlay);emit(clipped.get(i),poses,output,argb,light,overlay);emit(clipped.get(i+1),poses,output,argb,light,overlay);emit(clipped.get(i+1),poses,output,argb,light,overlay);}
        }
    }
    private static void emit(Vertex v,PoseStack poses,VertexConsumer output,int color,int light,int overlay){output.addVertex(poses.last().pose(),v.x,v.y,v.z).setColor(color).setUv(0,0).setOverlay(overlay).setLight(light).setNormal(poses.last(),v.nx,v.ny,v.nz);}
    private RefuelerModels(){}
}
