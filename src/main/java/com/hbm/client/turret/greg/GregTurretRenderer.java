package com.hbm.client.turret.greg;
import com.hbm.client.model.*;
import com.hbm.machine.turret.greg.GregTurretEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
/** Exact source RenderTurretArty:fixedBase,Yyaw-180,XpitchaboutY3,Cannon,positiveZ2.5barrelrecoil. */
public final class GregTurretRenderer implements BlockEntityRenderer<GregTurretEntity> {
 private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/turrets/turret_arty.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/turrets/arty.png");private static WavefrontMesh source,base;
 public GregTurretRenderer(BlockEntityRendererProvider.Context context){}
 private static WavefrontMesh base(WavefrontMesh mesh){if(source!=mesh){source=mesh;var faces=new ArrayList<WavefrontMesh.Face>();var triangles=new ArrayList<WavefrontMesh.Triangle>();var map=new HashMap<Integer,Integer>();for(int i=0;i<mesh.faces().size();i++)if(mesh.faces().get(i).legacyPart().equalsIgnoreCase("Base")){map.put(i,faces.size());faces.add(mesh.faces().get(i));}for(var triangle:mesh.triangles()){var index=map.get(triangle.face());if(index!=null)triangles.add(new WavefrontMesh.Triangle(index,triangle.first(),triangle.second(),triangle.third()));}base=new WavefrontMesh(mesh.positions(),mesh.textureCoordinates(),mesh.normals(),faces,triangles,mesh.materialLibraries(),mesh.nonFaceElements(),mesh.otherDirectives());}return base;}
 private static void part(WavefrontMesh mesh,PoseStack pose,MultiBufferSource buffers,int light,int overlay,String part){LegacyObjRenderer.renderOnly(mesh,pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR,part);}
 @Override public void render(GregTurretEntity tile,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);var offset=tile.horizontalOffset();LegacyContactShadows.render(tile,base(mesh),new org.joml.Matrix4f().translation((float)offset.x,0,(float)offset.z),pose,buffers);pose.pushPose();pose.translate(offset.x,0,offset.z);part(mesh,pose,buffers,light,overlay,"Base");double yaw=-Math.toDegrees(Mth.lerp(partial,tile.lastRotationYaw,tile.rotationYaw))-90,pitch=Math.toDegrees(Mth.lerp(partial,tile.lastRotationPitch,tile.rotationPitch));pose.mulPose(Axis.YP.rotationDegrees((float)(yaw-90)));part(mesh,pose,buffers,light,overlay,"Carriage");pose.translate(0,3,0);pose.mulPose(Axis.XP.rotationDegrees((float)pitch));pose.translate(0,-3,0);part(mesh,pose,buffers,light,overlay,"Cannon");pose.translate(0,0,Mth.lerp(partial,tile.lastBarrelPos,tile.barrelPos)*2.5);part(mesh,pose,buffers,light,overlay,"Barrel");pose.popPose();}
 @Override public boolean shouldRenderOffScreen(GregTurretEntity tile){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(GregTurretEntity tile){return new AABB(Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY);}
}
