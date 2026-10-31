package com.hbm.client.model;

import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/** Contact-only shadows projected from actual downward OBJ faces, never proxy collision boxes. */
public final class LegacyContactShadows {
    private static final float GAP=.35F;
    private static final ResourceLocation SHADOW=ResourceLocation.withDefaultNamespace("textures/misc/shadow.png");
    private record Point(float x,float y,float z) { }
    private record Triangle(Point a,Point b,Point c) { }
    private record Patch(Point a,Point b,Point c,float height,float weight) { }
    private record Sample(float x,float z,float weight) { }
    // A tiny circular penumbra makes a contact shadow visible beyond an opaque footplate.
    // Every sample still projects the actual mesh and clips to a real supporting surface.
    private static final float RADIUS=.125F,DIAGONAL=RADIUS*.70710678F;
    private static final List<Sample> PENUMBRA=List.of(new Sample(0,0,.2F),
            new Sample(RADIUS,0,.1F),new Sample(-RADIUS,0,.1F),new Sample(0,RADIUS,.1F),new Sample(0,-RADIUS,.1F),
            new Sample(DIAGONAL,DIAGONAL,.1F),new Sample(-DIAGONAL,DIAGONAL,.1F),new Sample(DIAGONAL,-DIAGONAL,.1F),new Sample(-DIAGONAL,-DIAGONAL,.1F));
    private record Support(BlockPos pos,BlockState state) { }
    private static final IdentityHashMap<WavefrontMesh,Map<Matrix4f,List<Triangle>>> MESHES=new IdentityHashMap<>();
    private static final WeakHashMap<BlockEntity,Projection> PROJECTIONS=new WeakHashMap<>();
    private static final class Projection {
        final WavefrontMesh mesh;
        final Matrix4f transform;
        final List<Support> supports;
        final float[] positions;
        final int[] alphas;
        long checkedTick;

        Projection(WavefrontMesh mesh,Matrix4f transform,List<Support> supports,List<Patch> patches,long tick) {
            this.mesh=mesh;
            this.transform=new Matrix4f(transform);
            this.supports=supports;
            positions=new float[patches.size()*12];
            alphas=new int[patches.size()*4];
            checkedTick=tick;
            int vertex=0;
            for(Patch patch:patches)for(int corner=0;corner<4;corner++) {
                Point point=corner==0?patch.a():corner==1?patch.c():patch.b();
                positions[vertex*3]=point.x();
                positions[vertex*3+1]=patch.height()+.002F;
                positions[vertex*3+2]=point.z();
                alphas[vertex++]=Mth.clamp(Math.round(112*patch.weight()*(1-Math.max(0,point.y()-patch.height())/GAP)),0,112);
            }
        }
    }
    private LegacyContactShadows() { }
    public static void clear(){MESHES.clear();PROJECTIONS.clear();}

    public static void render(BlockEntity machine,WavefrontMesh mesh,Matrix4f transform,PoseStack poses,MultiBufferSource buffers) {
        var level=machine.getLevel();if(level==null)return;
        Projection projection=PROJECTIONS.get(machine);
        boolean rebuild=projection==null||projection.mesh!=mesh||!projection.transform.equals(transform);
        long tick=level.getGameTime();
        // Main and shader passes share this projection; supporting blocks need one check per tick.
        if(!rebuild&&projection.checkedTick!=tick) {
            projection.checkedTick=tick;
            for(Support support:projection.supports)if(level.getBlockState(support.pos())!=support.state()) {
                rebuild=true;
                break;
            }
        }
        if(rebuild) {
            projection=project(machine,mesh,transform);PROJECTIONS.put(machine,projection);
        }
        if(projection.alphas.length==0)return;
        var vertices=buffers.getBuffer(RenderType.entityShadow(SHADOW));var pose=poses.last();
        for(int i=0;i<projection.alphas.length;i++) {
            vertices.addVertex(pose,projection.positions[i*3],projection.positions[i*3+1],projection.positions[i*3+2])
                    .setColor(255,255,255,projection.alphas[i]).setUv(.5F,.5F)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(pose,0,1,0);
        }
    }

    private static Projection project(BlockEntity machine,WavefrontMesh mesh,Matrix4f transform) {
        var triangles=MESHES.computeIfAbsent(mesh,ignored->new HashMap<>()).computeIfAbsent(new Matrix4f(transform),key->{
            var result=new ArrayList<Triangle>();
            for(var triangle:mesh.triangles()) {
                var face=mesh.faces().get(triangle.face());
                Point a=point(mesh,face.corners().get(triangle.first()).position(),key),b=point(mesh,face.corners().get(triangle.second()).position(),key),c=point(mesh,face.corners().get(triangle.third()).position(),key);
                // The Y component of (b-a)x(c-a); vertical/upward faces cast no contact decal.
                if((b.z()-a.z())*(c.x()-a.x())-(b.x()-a.x())*(c.z()-a.z())<-.000001F)result.add(new Triangle(a,b,c));
            }
            return List.copyOf(result);
        });
        var level=machine.getLevel();var origin=machine.getBlockPos();
        Map<BlockPos,BlockState> supports=new LinkedHashMap<>();var patches=new ArrayList<Patch>();
        Map<BlockPos,List<AABB>> collisionBoxes=new HashMap<>();
        for(Triangle source:triangles)for(Sample sample:PENUMBRA) {
            Triangle triangle=new Triangle(offset(source.a(),sample),offset(source.b(),sample),offset(source.c(),sample));
            float minY=Math.min(triangle.a().y(),Math.min(triangle.b().y(),triangle.c().y()));
            float maxY=Math.max(triangle.a().y(),Math.max(triangle.b().y(),triangle.c().y()));
            int minX=Mth.floor(Math.min(triangle.a().x(),Math.min(triangle.b().x(),triangle.c().x()))),maxX=Mth.floor(Math.max(triangle.a().x(),Math.max(triangle.b().x(),triangle.c().x())));
            int minZ=Mth.floor(Math.min(triangle.a().z(),Math.min(triangle.b().z(),triangle.c().z()))),maxZ=Mth.floor(Math.max(triangle.a().z(),Math.max(triangle.b().z(),triangle.c().z())));
            int firstY=Mth.floor(maxY+.001F),lastY=Mth.floor(minY-GAP)-1;
            for(int x=minX;x<=maxX;x++)for(int z=minZ;z<=maxZ;z++)for(int y=firstY;y>=lastY;y--) {
                BlockPos pos=origin.offset(x,y,z);BlockState state=supports.computeIfAbsent(pos,level::getBlockState);
                if(state.getBlock() instanceof DummyableBlock||state.isAir())continue;
                for(AABB box:collisionBoxes.computeIfAbsent(pos,key->state.getCollisionShape(level,key).toAabbs())) {
                    float top=(float)(y+box.maxY);
                    if(top>maxY+.001F||minY-top>GAP)continue;
                    List<Point> polygon=new ArrayList<>(List.of(triangle.a(),triangle.b(),triangle.c()));
                    polygon=clip(polygon,0,(float)(x+box.minX),true);polygon=clip(polygon,0,(float)(x+box.maxX),false);
                    polygon=clip(polygon,2,(float)(z+box.minZ),true);polygon=clip(polygon,2,(float)(z+box.maxZ),false);
                    polygon=clip(polygon,1,top-.001F,true);polygon=clip(polygon,1,top+GAP,false);
                    for(int i=1;i+1<polygon.size();i++)patches.add(new Patch(polygon.getFirst(),polygon.get(i),polygon.get(i+1),top,sample.weight()));
                }
            }
        }
        return new Projection(mesh,transform,supports.entrySet().stream().map(e->new Support(e.getKey(),e.getValue())).toList(),patches,level.getGameTime());
    }
    private static Point offset(Point point,Sample sample){return new Point(point.x()+sample.x(),point.y(),point.z()+sample.z());}
    private static Point point(WavefrontMesh mesh,int index,Matrix4f transform) {
        var p=mesh.positions().get(index);var v=transform.transformPosition(new Vector3f(p.x(),p.y(),p.z()));return new Point(v.x,v.y,v.z);
    }
    private static float axis(Point p,int axis){return axis==0?p.x():axis==1?p.y():p.z();}
    private static List<Point> clip(List<Point> input,int axis,float boundary,boolean minimum) {
        if(input.isEmpty())return input;var result=new ArrayList<Point>();Point previous=input.getLast();
        boolean previousInside=minimum?axis(previous,axis)>=boundary:axis(previous,axis)<=boundary;
        for(Point current:input) {
            boolean inside=minimum?axis(current,axis)>=boundary:axis(current,axis)<=boundary;
            if(inside!=previousInside){float t=(boundary-axis(previous,axis))/(axis(current,axis)-axis(previous,axis));result.add(new Point(previous.x()+(current.x()-previous.x())*t,previous.y()+(current.y()-previous.y())*t,previous.z()+(current.z()-previous.z())*t));}
            if(inside)result.add(current);previous=current;previousInside=inside;
        }
        return result;
    }
}
