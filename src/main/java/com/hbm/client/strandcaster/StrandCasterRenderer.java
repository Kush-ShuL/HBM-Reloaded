package com.hbm.client.strandcaster;
import com.hbm.client.model.*;
import com.hbm.machine.strandcaster.StrandCasterEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Exact original caster/plate mesh, metal level, clipped emerging strand and HFR smooth UVs. */
public final class StrandCasterRenderer implements BlockEntityRenderer<StrandCasterEntity> {
 static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/strand_caster.png"),LAVA=ResourceLocation.parse("hbm:textures/models/machines/lava_gray.png");
 public StrandCasterRenderer(BlockEntityRendererProvider.Context context){}
 static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/machines/strand_caster.obj"));}
 static void part(String name,PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.render(model(),pose,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,WavefrontMesh.legacyPart(name));}
 static void inventory(PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(model(),poses,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);}
 @Override public void render(StrandCasterEntity caster,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){float yaw=switch(DummyableBlock.facing(caster.getBlockState())){case WEST->90;case SOUTH->180;case EAST->270;default->0;};var transform=new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)).translate(.5F,0,.5F).rotateY((float)Math.PI);LegacyContactShadows.render(caster,model(),transform,pose,buffers);pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(yaw));pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(180));part("caster",pose,buffers,light,overlay);var mold=caster.mold();if(caster.amount!=0&&mold!=null&&caster.type!=null){double level=(double)caster.amount/caster.capacity()*.675,offset=(double)caster.amount/mold.requiredUnits()*.375;int color=0xff000000|caster.type.moltenColor();plate(pose,buffers,overlay,color,Math.max(-offset+3.4,0));var vc=buffers.getBuffer(RenderType.entityCutoutNoCull(LAVA));float y=(float)(2.3+level);vertex(vc,pose,-.9F,y,-.999F,0,0,0,1,0,color,overlay);vertex(vc,pose,-.9F,y,.999F,0,1,0,1,0,color,overlay);vertex(vc,pose,.9F,y,.999F,1,1,0,1,0,color,overlay);vertex(vc,pose,.9F,y,-.999F,1,0,0,1,0,color,overlay);}pose.popPose();}
 // glClipPlane(0,0,-1,.5), captured BEFORE source translated strand offset. CPU clipping
 // keeps the original plane and interpolates each authored UV/normal at its exact intersection.
 private record V(float x,float y,float z,float u,float v,float nx,float ny,float nz){V mix(V b,float t){return new V(x+(b.x-x)*t,y+(b.y-y)*t,z+(b.z-z)*t,u+(b.u-u)*t,v+(b.v-v)*t,nx+(b.nx-nx)*t,ny+(b.ny-ny)*t,nz+(b.nz-nz)*t);}}
 private static void plate(PoseStack pose,MultiBufferSource buffers,int overlay,int color,double offset){var mesh=model();var vc=buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));for(var face:mesh.faces()){if(!face.legacyPart().equalsIgnoreCase("plate"))continue;var polygon=new ArrayList<V>();for(var corner:face.corners()){var p=mesh.positions().get(corner.position());var uv=corner.texture()<0?new WavefrontMesh.TexCoord(0,0,0):mesh.textureCoordinates().get(corner.texture());var n=corner.normal()<0?face.normal():mesh.normals().get(corner.normal());polygon.add(new V(p.x(),p.y(),p.z()+(float)offset,uv.u(),1-uv.v(),n.x(),n.y(),n.z()));}var clipped=new ArrayList<V>();for(int i=0;i<polygon.size();i++){var a=polygon.get(i);var b=polygon.get((i+1)%polygon.size());boolean ai=a.z<=.5F,bi=b.z<=.5F;if(ai)clipped.add(a);if(ai!=bi)clipped.add(a.mix(b,(.5F-a.z)/(b.z-a.z)));}for(int i=1;i+1<clipped.size();i++){emit(vc,pose,clipped.getFirst(),color,overlay);emit(vc,pose,clipped.get(i),color,overlay);emit(vc,pose,clipped.get(i+1),color,overlay);emit(vc,pose,clipped.get(i+1),color,overlay);}}}
 private static void emit(VertexConsumer vc,PoseStack pose,V v,int color,int overlay){vertex(vc,pose,v.x,v.y,v.z,v.u,v.v,v.nx,v.ny,v.nz,color,overlay);}
 private static void vertex(VertexConsumer vc,PoseStack pose,float x,float y,float z,float u,float v,float nx,float ny,float nz,int color,int overlay){vc.addVertex(pose.last(),x,y,z).setColor(color).setUv(u,v).setOverlay(overlay).setLight(LightTexture.FULL_BRIGHT).setNormal(pose.last(),nx,ny,nz);}
 @Override public boolean shouldRenderOffScreen(StrandCasterEntity caster){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(StrandCasterEntity caster){var p=caster.getBlockPos();return new AABB(p.getX()-7,p.getY(),p.getZ()-7,p.getX()+7,p.getY()+3,p.getZ()+7);}
}
