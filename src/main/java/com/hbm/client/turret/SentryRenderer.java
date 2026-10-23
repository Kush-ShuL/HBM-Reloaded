package com.hbm.client.turret;
import com.hbm.client.model.*;
import com.hbm.machine.turret.SentryEntity;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
/** Literal RenderTurretSentry hierarchy, original HFR authored normals/UVs and independent recoil tracks. */
public final class SentryRenderer implements BlockEntityRenderer<SentryEntity> {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/turrets/turret_sentry.obj"),NORMAL=ResourceLocation.parse("hbm:textures/models/turrets/sentry.png"),DAMAGED=ResourceLocation.parse("hbm:textures/models/turrets/sentry_damaged.png");
    private static WavefrontMesh shadowSource,shadowBase;
    public SentryRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh mesh(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    private static WavefrontMesh shadow(WavefrontMesh mesh){if(shadowSource!=mesh){shadowSource=mesh;var faces=new ArrayList<WavefrontMesh.Face>();var triangles=new ArrayList<WavefrontMesh.Triangle>();var remap=new HashMap<Integer,Integer>();for(int i=0;i<mesh.faces().size();i++)if(mesh.faces().get(i).legacyPart().equalsIgnoreCase("Base")){remap.put(i,faces.size());faces.add(mesh.faces().get(i));}for(var triangle:mesh.triangles()){var next=remap.get(triangle.face());if(next!=null)triangles.add(new WavefrontMesh.Triangle(next,triangle.first(),triangle.second(),triangle.third()));}shadowBase=new WavefrontMesh(mesh.positions(),mesh.textureCoordinates(),mesh.normals(),faces,triangles,mesh.materialLibraries(),mesh.nonFaceElements(),mesh.otherDirectives());}return shadowBase;}
    private static void part(WavefrontMesh mesh,PoseStack pose,VertexConsumer out,int light,int overlay,String... names){LegacyObjRenderer.renderOnly(mesh,pose,out,light,overlay,-1,LegacyObjRenderer.Convention.HFR,names);}
    static void item(PoseStack pose,MultiBufferSource buffers,int light,int overlay){part(mesh(),pose,buffers.getBuffer(RenderType.entityCutout(NORMAL)),light,overlay,"Base","Pivot","Body","Drum","BarrelL","BarrelR");}
    @Override public void render(SentryEntity turret,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){var mesh=mesh();LegacyContactShadows.render(turret,shadow(mesh),new org.joml.Matrix4f().translation(.5F,0,.5F),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);var out=buffers.getBuffer(RenderType.entityCutout(turret.damaged()?DAMAGED:NORMAL));part(mesh,pose,out,light,overlay,"Base");pose.mulPose(Axis.YP.rotationDegrees((float)-Math.toDegrees(Mth.lerp(partial,turret.lastRotationYaw,turret.rotationYaw))));part(mesh,pose,out,light,overlay,"Pivot");pose.translate(0,1.25,0);pose.mulPose(Axis.XP.rotationDegrees((float)-Math.toDegrees(Mth.lerp(partial,turret.lastRotationPitch,turret.rotationPitch))));pose.translate(0,-1.25,0);part(mesh,pose,out,light,overlay,"Body","Drum");pose.pushPose();pose.translate(0,0,Mth.lerp(partial,turret.lastBarrelLeftPos,turret.barrelLeftPos)*-.5);part(mesh,pose,out,light,overlay,"BarrelL");pose.popPose();pose.pushPose();if(turret.damaged()){pose.translate(0,1.5,.5);pose.mulPose(Axis.XP.rotationDegrees(25));pose.translate(0,-1.5,-.5);}else pose.translate(0,0,Mth.lerp(partial,turret.lastBarrelRightPos,turret.barrelRightPos)*-.5);part(mesh,pose,out,light,overlay,"BarrelR");pose.popPose();pose.popPose();}
    @Override public boolean shouldRenderOffScreen(SentryEntity turret){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(SentryEntity turret){return new AABB(Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY);}
}
