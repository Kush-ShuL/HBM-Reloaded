package com.hbm.client.door.slidingblast;

import com.hbm.client.animation.bus.BusAnimationSequence;
import com.hbm.client.animation.bus.BusAnimationKeyframe.IType;
import com.hbm.client.model.*;
import com.hbm.door.generic.GenericDoorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** Actual source RenderSlidingBlastDoor and original bus sequences; source two clipping planes survive modern core GL. */
public final class SlidingBlastDoorModel {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/pheodoors/blast_door.obj");
    private static final String[] SKINS={"blast_door","blast_door_industrial"};
    private static final BusAnimationSequence OPEN_LOCK=new BusAnimationSequence().setPos(0,0,0).addPos(1,0,0,200);
    private static final BusAnimationSequence OPEN_DOOR=new BusAnimationSequence().setPos(0,0,0).addPos(0,0,0,350).addPos(0,.05,0,200).addPos(0,1,0,650,IType.SIN_UP);
    private static final BusAnimationSequence CLOSE_LOCK=new BusAnimationSequence().setPos(1,0,0).addPos(1,0,0,1000).addPos(0,0,0,200);
    private static final BusAnimationSequence CLOSE_DOOR=new BusAnimationSequence().setPos(0,1,0).addPos(0,.05,0,650,IType.SIN_UP).addPos(0,0,0,200);
    private static WavefrontMesh preparedMesh;
    private static final Map<String,List<float[][]>> PARTS=new HashMap<>();
    public static ResourceLocation texture(int skin){return ResourceLocation.parse("hbm:textures/models/pheodoors/"+SKINS[Math.abs(skin)%SKINS.length]+".png");}
    public static void world(GenericDoorBlockEntity door,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        double open=door.state==GenericDoorBlockEntity.OPEN?2.125:0,lock=door.state==GenericDoorBlockEntity.OPEN?90:0;
        if(door.animStartTime!=0){
            int millis=(int)Math.min(Integer.MAX_VALUE,Math.max(0,System.currentTimeMillis()-door.animStartTime));
            boolean opening=door.state==GenericDoorBlockEntity.OPENING||door.state==GenericDoorBlockEntity.OPEN;
            open=(opening?OPEN_DOOR:CLOSE_DOOR).getTransformation(millis)[1]*2.125;
            lock=(opening?OPEN_LOCK:CLOSE_LOCK).getTransformation(millis)[0]*90;
        }
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);
        var consumer=buffers.getBuffer(RenderType.entityCutoutNoCull(texture(door.skinIndex)));
        LegacyObjRenderer.renderOnly(mesh,poses,consumer,light,overlay,-1,LegacyObjRenderer.Convention.HFR,"Frame");
        double travel=Mth.clamp(open,0,2.125);
        Scratch clip=new Scratch();
        part(mesh,"LeftDoor",travel,0,clip,poses,consumer,light,overlay);
        part(mesh,"RightLock",travel,90+lock,clip,poses,consumer,light,overlay);
        part(mesh,"RightDoor",-travel,0,clip,poses,consumer,light,overlay);
        part(mesh,"LeftLock",-travel,90+lock,clip,poses,consumer,light,overlay);
    }
    public static void item(PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        int skin=(int)((System.currentTimeMillis()%(SKINS.length*1000))/1000);
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);
        // Source inventory draws the entire unanimated mesh, with neither world clip plane nor added common yaw.
        LegacyObjRenderer.renderAll(mesh,poses,buffers.getBuffer(RenderType.entityCutout(texture(skin))),light,overlay,-1,LegacyObjRenderer.Convention.HFR);
    }
    private static List<float[][]> triangles(WavefrontMesh mesh,String name){
        if(preparedMesh!=mesh){PARTS.clear();preparedMesh=mesh;}
        return PARTS.computeIfAbsent(name,key->{
            List<float[][]> result=new ArrayList<>();
            for(var triangle:mesh.triangles()){
                var face=mesh.faces().get(triangle.face());
                if(!face.legacyPart().equalsIgnoreCase(key))continue;
                int[] indices={triangle.first(),triangle.second(),triangle.third()};float[][] vertices=new float[3][8];
                for(int index=0;index<3;index++){
                    var corner=face.corners().get(indices[index]);var position=mesh.positions().get(corner.position());
                    var normal=corner.normal()>=0?mesh.normals().get(corner.normal()):face.normal();
                    float[] vertex=vertices[index];vertex[0]=position.x();vertex[1]=position.y();vertex[2]=position.z();
                    if(corner.texture()>=0){var uv=mesh.textureCoordinates().get(corner.texture());vertex[3]=uv.u();vertex[4]=1-uv.v();}
                    vertex[5]=normal.x();vertex[6]=normal.y();vertex[7]=normal.z();
                }
                result.add(vertices);
            }
            return result;
        });
    }
    private static void part(WavefrontMesh mesh,String name,double move,double angle,Scratch scratch,PoseStack poses,VertexConsumer out,int light,int overlay){
        double radians=Math.toRadians(angle),sin=Math.sin(radians),cos=Math.cos(radians);
        for(var triangle:triangles(mesh,name)){
            for(int index=0;index<3;index++){
                float[] from=triangle[index],to=scratch.a[index];System.arraycopy(from,0,to,0,8);
                if(angle!=0){
                    double y=from[1]-1.8125,z=from[2];
                    to[1]=(float)(y*cos-z*sin+1.8125);to[2]=(float)(y*sin+z*cos+move);
                    to[6]=(float)(from[6]*cos-from[7]*sin);to[7]=(float)(from[6]*sin+from[7]*cos);
                }else to[2]=(float)(from[2]+move);
            }
            // Source GL_CLIP_PLANE0 (0,0,1,2.5), then GL_CLIP_PLANE1 (0,0,-1,2.5), defined before part transforms.
            int count=clip(scratch.a,3,scratch.b,1);count=clip(scratch.b,count,scratch.c,-1);
            for(int index=1;index+1<count;index++){
                emit(scratch.c[0],poses.last(),out,light,overlay);emit(scratch.c[index],poses.last(),out,light,overlay);
                emit(scratch.c[index+1],poses.last(),out,light,overlay);emit(scratch.c[index+1],poses.last(),out,light,overlay);
            }
        }
    }
    private static int clip(float[][] source,int count,float[][] target,int sign){
        if(count==0)return 0;int written=0;float[] previous=source[count-1];double previousDistance=previous[2]*sign+2.5;
        for(int index=0;index<count;index++){
            float[] current=source[index];double distance=current[2]*sign+2.5;
            if((distance>=0)!=(previousDistance>=0)){
                double fraction=previousDistance/(previousDistance-distance);float[] intersection=target[written++];
                for(int component=0;component<8;component++)intersection[component]=(float)(previous[component]+fraction*(current[component]-previous[component]));
            }
            if(distance>=0)System.arraycopy(current,0,target[written++],0,8);
            previous=current;previousDistance=distance;
        }
        return written;
    }
    private static void emit(float[] vertex,PoseStack.Pose pose,VertexConsumer out,int light,int overlay){
        out.addVertex(pose,vertex[0],vertex[1],vertex[2]).setColor(-1).setUv(vertex[3],vertex[4]).setOverlay(overlay).setLight(light).setNormal(pose,vertex[5],vertex[6],vertex[7]);
    }
    private static final class Scratch{final float[][]a=new float[8][8],b=new float[8][8],c=new float[8][8];}
    private SlidingBlastDoorModel(){}
}
