package com.hbm.client.door.family;

import com.hbm.client.animation.bus.BusAnimationSequence;
import com.hbm.client.animation.bus.BusAnimationKeyframe.IType;
import com.hbm.client.model.*;
import com.hbm.door.generic.*;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** The seven real source Render*Door transforms, original BusAnimationSequence math, mesh/skin arrays and GL clip planes. */
public final class GenericDoorFamilyModels {
    private record Source(String model,String[] skins,boolean noCull){}
    private static final Map<GenericDoorDeclaration,Source> SOURCES=Map.of(
        GenericDoorDeclaration.LARGE_VEHICLE_DOOR,new Source("vehicle_door",new String[]{"vehicle_door","vehicle_door_black","vehicle_door_blue","vehicle_door_red"},true),
        GenericDoorDeclaration.SECURE_ACCESS_DOOR,new Source("secure_door",new String[]{"secure_door","secure_door_grey","secure_door_black","secure_door_yellow"},false),
        GenericDoorDeclaration.ROUND_AIRLOCK_DOOR,new Source("airlock_door",new String[]{"airlock_door","airlock_door_clean","airlock_door_green"},true),
        GenericDoorDeclaration.QE_CONTAINMENT,new Source("containment_door",new String[]{"containment_door","containment_door_trefoil","containment_door_trefoil_yellow"},false),
        GenericDoorDeclaration.QE_SLIDING,new Source("sliding_door",new String[]{"sliding_door","sliding_door_black","sliding_door_grid","sliding_door_plate","sliding_door_white"},true),
        GenericDoorDeclaration.SLIDING_SEAL_DOOR,new Source("seal_door",new String[]{"seal_door","seal_door_clean","seal_door_trefoil"},false),
        GenericDoorDeclaration.WATER_DOOR,new Source("water_door",new String[]{"water_door","water_door_clean"},false));
    private static final Map<GenericDoorDeclaration,BusAnimationSequence[]> LINEAR=new EnumMap<>(GenericDoorDeclaration.class);
    private static final BusAnimationSequence WATER_OPEN_DOOR=new BusAnimationSequence().setPos(0,0,0).addPos(0,0,0,1500).addPos(0,1,0,1500,IType.SIN_FULL),WATER_OPEN_BOLT=new BusAnimationSequence().setPos(0,0,0).addPos(0,0,1,1500,IType.SIN_FULL),WATER_CLOSE_DOOR=new BusAnimationSequence().setPos(0,1,0).addPos(0,0,0,1500,IType.SIN_FULL),WATER_CLOSE_BOLT=new BusAnimationSequence().setPos(0,0,1).addPos(0,0,1,1200).addPos(0,0,0,1500,IType.SIN_FULL);
    private static WavefrontMesh mesh(GenericDoorDeclaration declaration){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/pheodoors/"+SOURCES.get(declaration).model+".obj"));}
    private static ResourceLocation texture(GenericDoorDeclaration declaration,int skin){var skins=SOURCES.get(declaration).skins;return ResourceLocation.parse("hbm:textures/models/pheodoors/"+skins[Math.abs(skin)%skins.length]+".png");}
    private static double fraction(GenericDoorBlockEntity door,int millis,boolean opening){var sequences=LINEAR.computeIfAbsent(door.declaration(),declaration->new BusAnimationSequence[]{new BusAnimationSequence().setPos(0,0,0).addPos(0,1,0,declaration.timeToOpen()*50),new BusAnimationSequence().setPos(0,1,0).addPos(0,0,0,declaration.timeToOpen()*50)});return door.animStartTime==0?(door.state==GenericDoorBlockEntity.OPEN?1:0):sequences[opening?0:1].getTransformation(millis)[1];}
    private static void part(WavefrontMesh mesh,String part,PoseStack pose,VertexConsumer out,int light,int overlay){LegacyObjRenderer.renderOnly(mesh,pose,out,light,overlay,-1,LegacyObjRenderer.Convention.HFR,part);}
    public static void world(GenericDoorBlockEntity door,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        var declaration=door.declaration();var source=SOURCES.get(declaration);var mesh=mesh(declaration);var out=buffers.getBuffer(source.noCull?RenderType.entityCutoutNoCull(texture(declaration,door.skinIndex)):RenderType.entityCutout(texture(declaration,door.skinIndex)));int millis=(int)Math.min(Integer.MAX_VALUE,Math.max(0,System.currentTimeMillis()-door.animStartTime));boolean opening=door.state==GenericDoorBlockEntity.OPENING||door.state==GenericDoorBlockEntity.OPEN;double amount=fraction(door,millis,opening);var scratch=new Scratch();
        switch(declaration){
            case LARGE_VEHICLE_DOOR->{pose.mulPose(Axis.YP.rotationDegrees(90));part(mesh,"Frame",pose,out,light,overlay);double move=Mth.clamp(amount*3,0,3);double[][]planes={{1,0,0,3.4375},{-1,0,0,3.4375}};clipped(mesh,"Left",-move,0,0,planes,scratch,pose,out,light,overlay);clipped(mesh,"Right",move,0,0,planes,scratch,pose,out,light,overlay);}
            case ROUND_AIRLOCK_DOOR->{pose.translate(0,0,.5);part(mesh,"Frame",pose,out,light,overlay);double move=Mth.clamp(amount*1.5,0,1.5);double[][]planes={{0,0,1,1.999},{0,0,-1,1.999}};clipped(mesh,"Left",0,0,move,planes,scratch,pose,out,light,overlay);clipped(mesh,"Right",0,0,-move,planes,scratch,pose,out,light,overlay);}
            case QE_SLIDING->{pose.translate(.53125,.001,.5);part(mesh,"Frame",pose,out,light,overlay);double move=Mth.clamp(amount*.95,0,.95);pose.pushPose();pose.translate(0,0,move);part(mesh,"Left",pose,out,light,overlay);pose.popPose();pose.pushPose();pose.translate(0,0,-move);part(mesh,"Right",pose,out,light,overlay);pose.popPose();}
            case QE_CONTAINMENT->{pose.translate(.25,0,0);part(mesh,"Frame",pose,out,light,overlay);clipped(mesh,"Door",0,Mth.clamp(amount*2.25,0,2.25),0,new double[][]{{0,-1,0,3}},scratch,pose,out,light,overlay);}
            case SLIDING_SEAL_DOOR->{pose.translate(.5,0,0);part(mesh,"Frame",pose,out,light,overlay);double clamped=Mth.clamp(amount,0,1);clipped(mesh,"Door",0,0,clamped*clamped*(3-2*clamped)*.9,new double[][]{{0,0,-1,.5001}},scratch,pose,out,light,overlay);}
            case SECURE_ACCESS_DOOR->{pose.translate(0,1,0);part(mesh,"Frame",pose,out,light,overlay);pose.translate(0,Mth.clamp(amount*3.5,0,3.5),0);part(mesh,"Door",pose,out,light,overlay);}
            case WATER_DOOR->{double rotation=door.state==GenericDoorBlockEntity.OPEN?120:0,bolt=door.state==GenericDoorBlockEntity.OPEN?1:0;if(door.animStartTime!=0){rotation=(opening?WATER_OPEN_DOOR:WATER_CLOSE_DOOR).getTransformation(millis)[1]*120;bolt=(opening?WATER_OPEN_BOLT:WATER_CLOSE_BOLT).getTransformation(millis)[2];}pose.translate(.375,0,0);pose.mulPose(Axis.YP.rotationDegrees(90));part(mesh,"Frame",pose,out,light,overlay);pose.translate(-1.1875,0,0);pose.mulPose(Axis.YP.rotationDegrees((float)-rotation));pose.translate(1.1875,0,0);part(mesh,"Door_Cube.003",pose,out,light,overlay);pose.pushPose();pose.translate(-.4*bolt,0,0);part(mesh,"Bolts",pose,out,light,overlay);pose.popPose();for(double y:new double[]{2.28125,.71875}){pose.pushPose();pose.translate(.40625F,y,0);pose.mulPose(Axis.ZP.rotationDegrees((float)(bolt*360)));pose.translate(-.40625F,-y,0);part(mesh,y>1?"Top":"Bottom",pose,out,light,overlay);pose.popPose();}}
            default->throw new IllegalArgumentException("Not a source247 door: "+declaration);
        }
    }
    public static void item(GenericDoorDeclaration declaration,PoseStack pose,MultiBufferSource buffers,int light,int overlay){var source=SOURCES.get(declaration);int skin=(int)((System.currentTimeMillis()%(source.skins.length*1000))/1000);if(declaration==GenericDoorDeclaration.LARGE_VEHICLE_DOOR||declaration==GenericDoorDeclaration.WATER_DOOR)pose.mulPose(Axis.YP.rotationDegrees(90));if(declaration==GenericDoorDeclaration.SECURE_ACCESS_DOOR)pose.translate(0,1,0);LegacyObjRenderer.renderAll(mesh(declaration),pose,buffers.getBuffer(RenderType.entityCutout(texture(declaration,skin))),light,overlay,-1,LegacyObjRenderer.Convention.HFR);}
    public static float inventoryY(GenericDoorDeclaration declaration){return switch(declaration){case LARGE_VEHICLE_DOOR,WATER_DOOR->-4;case SECURE_ACCESS_DOOR,ROUND_AIRLOCK_DOOR->-3.75F;case QE_CONTAINMENT->-3.5F;case QE_SLIDING->-3;case SLIDING_SEAL_DOOR->-5;default->throw new IllegalArgumentException();};}
    public static float inventoryScale(GenericDoorDeclaration declaration){return switch(declaration){case LARGE_VEHICLE_DOOR->1.8F;case SECURE_ACCESS_DOOR->2.4F;case ROUND_AIRLOCK_DOOR->3;case QE_CONTAINMENT->3.8F;case QE_SLIDING->5;case SLIDING_SEAL_DOOR->7;case WATER_DOOR->4;default->throw new IllegalArgumentException();};}
    private static final Map<WavefrontMesh,Map<String,List<float[][]>>> TRIANGLES=new WeakHashMap<>();
    private static List<float[][]> triangles(WavefrontMesh mesh,String name){return TRIANGLES.computeIfAbsent(mesh,key->new HashMap<>()).computeIfAbsent(name,key->{var result=new ArrayList<float[][]>();for(var triangle:mesh.triangles()){var face=mesh.faces().get(triangle.face());if(!face.legacyPart().equalsIgnoreCase(name))continue;float[][] vertices=new float[3][8];int[]indices={triangle.first(),triangle.second(),triangle.third()};for(int i=0;i<3;i++){var corner=face.corners().get(indices[i]);var position=mesh.positions().get(corner.position());var normal=corner.normal()<0?face.normal():mesh.normals().get(corner.normal());float[]v=vertices[i];v[0]=position.x();v[1]=position.y();v[2]=position.z();if(corner.texture()>=0){var uv=mesh.textureCoordinates().get(corner.texture());v[3]=uv.u();v[4]=1-uv.v();}v[5]=normal.x();v[6]=normal.y();v[7]=normal.z();}result.add(vertices);}return result;});}
    /** Source planes are fixed before each moving translation; clip actual transformed triangles, interpolate original UV/normals, add no caps. */
    private static void clipped(WavefrontMesh mesh,String part,double dx,double dy,double dz,double[][]planes,Scratch scratch,PoseStack pose,VertexConsumer out,int light,int overlay){for(var triangle:triangles(mesh,part)){for(int i=0;i<3;i++){System.arraycopy(triangle[i],0,scratch.a[i],0,8);scratch.a[i][0]+=dx;scratch.a[i][1]+=dy;scratch.a[i][2]+=dz;}float[][]a=scratch.a,b=scratch.b;int count=3;for(var plane:planes){count=clip(a,count,b,plane);float[][]swap=a;a=b;b=swap;}for(int i=1;i+1<count;i++){emit(a[0],pose,out,light,overlay);emit(a[i],pose,out,light,overlay);emit(a[i+1],pose,out,light,overlay);emit(a[i+1],pose,out,light,overlay);}}}
    private static int clip(float[][]input,int count,float[][]output,double[]plane){if(count==0)return 0;int written=0;float[]previous=input[count-1];double prior=distance(previous,plane);for(int i=0;i<count;i++){float[]current=input[i];double now=distance(current,plane);if((now>=0)!=(prior>=0)){double t=prior/(prior-now);float[]intersection=output[written++];for(int component=0;component<8;component++)intersection[component]=(float)(previous[component]+t*(current[component]-previous[component]));}if(now>=0)System.arraycopy(current,0,output[written++],0,8);previous=current;prior=now;}return written;}
    private static double distance(float[]v,double[]plane){return v[0]*plane[0]+v[1]*plane[1]+v[2]*plane[2]+plane[3];}
    private static void emit(float[]v,PoseStack pose,VertexConsumer out,int light,int overlay){out.addVertex(pose.last(),v[0],v[1],v[2]).setColor(-1).setUv(v[3],v[4]).setOverlay(overlay).setLight(light).setNormal(pose.last(),v[5],v[6],v[7]);}
    private static final class Scratch{final float[][]a=new float[8][8],b=new float[8][8];}
    public static void clear(){TRIANGLES.clear();}
    private GenericDoorFamilyModels(){}
}
