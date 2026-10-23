package com.hbm.client.rockmill;
import com.hbm.client.model.*;
import com.hbm.machine.rockmill.RockMillEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Original HFR authored smooth normals, exact yaw and negative-Y wheel rotation. */
public final class RockMillRenderer implements BlockEntityRenderer<RockMillEntity> {
 static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/rockmill.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/rockmill.png");
 private static final Map<WavefrontMesh,Map<String,WavefrontMesh>> PARTS=new WeakHashMap<>();
 public RockMillRenderer(BlockEntityRendererProvider.Context context){}
 static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
 static void inventory(PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(model(),pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);}
 private static void part(PoseStack pose,MultiBufferSource buffers,int light,int overlay,String name){LegacyObjRenderer.renderOnly(model(),pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,name);}
 private static WavefrontMesh mesh(String part){var full=model();return PARTS.computeIfAbsent(full,key->new HashMap<>()).computeIfAbsent(part,key->{var faces=new ArrayList<WavefrontMesh.Face>();var triangles=new ArrayList<WavefrontMesh.Triangle>();var remap=new int[full.faces().size()];Arrays.fill(remap,-1);for(int i=0;i<remap.length;i++)if(full.faces().get(i).legacyPart().equals(part)){remap[i]=faces.size();faces.add(full.faces().get(i));}for(var triangle:full.triangles())if(remap[triangle.face()]>=0)triangles.add(new WavefrontMesh.Triangle(remap[triangle.face()],triangle.first(),triangle.second(),triangle.third()));return new WavefrontMesh(full.positions(),full.textureCoordinates(),full.normals(),faces,triangles,full.materialLibraries(),full.nonFaceElements(),full.otherDirectives());});}
 @Override public void render(RockMillEntity mill,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){int yaw=switch(DummyableBlock.facing(mill.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};float rotation=mill.prevRotation+(mill.rotation-mill.prevRotation)*partial;var transform=new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw));LegacyContactShadows.render(mill,mesh("Base"),transform,pose,buffers);if(mill.frame)LegacyContactShadows.render(mill,mesh("Frame"),transform,pose,buffers);LegacyContactShadows.render(mill,mesh("Wheel"),new org.joml.Matrix4f(transform).rotateY((float)Math.toRadians(-rotation)),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(yaw));part(pose,buffers,light,overlay,"Base");if(mill.frame)part(pose,buffers,light,overlay,"Frame");pose.mulPose(Axis.YN.rotationDegrees(rotation));part(pose,buffers,light,overlay,"Wheel");pose.popPose();}
 @Override public boolean shouldRenderOffScreen(RockMillEntity mill){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(RockMillEntity mill){var p=mill.getBlockPos();return new AABB(p.getX()-2,p.getY(),p.getZ()-2,p.getX()+3,p.getY()+3,p.getZ()+3);}
}
