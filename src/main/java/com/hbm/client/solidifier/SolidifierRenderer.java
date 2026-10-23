package com.hbm.client.solidifier;
import com.hbm.client.model.*;
import com.hbm.machine.solidifier.SolidifierEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Exact source Main/Fluid/Glass OBJ,authored smooth normals,unlit liquid height and source glass depth/blend. */
public final class SolidifierRenderer implements BlockEntityRenderer<SolidifierEntity> {
 private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/solidifier.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/solidifier.png");private static final Map<WavefrontMesh,WavefrontMesh> SHADOWS=new WeakHashMap<>();
 public SolidifierRenderer(BlockEntityRendererProvider.Context context){}static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
 private static float yaw(SolidifierEntity machine){return switch(machine.getBlockState().getValue(com.hbm.multiblock.DummyableBlock.LEGACY_META)-10){case 2->90;case 4->180;case 3->270;default->0;};}
 static void inventory(PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderOnly(model(),pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR,"Main");}
 private static WavefrontMesh shadow(WavefrontMesh m){return SHADOWS.computeIfAbsent(m,mesh->new WavefrontMesh(mesh.positions(),mesh.textureCoordinates(),mesh.normals(),mesh.faces(),mesh.triangles().stream().filter(t->mesh.faces().get(t.face()).legacyPart().equalsIgnoreCase("Main")).toList(),mesh.materialLibraries(),mesh.nonFaceElements(),mesh.otherDirectives()));}
 @Override public void render(SolidifierEntity machine,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){var mesh=model();LegacyContactShadows.render(machine,shadow(mesh),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw(machine))),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(yaw(machine)));var mainType=RenderType.entityCutoutNoCull(TEXTURE);LegacyObjRenderer.renderOnly(mesh,pose,buffers.getBuffer(mainType),light,overlay,-1,LegacyObjRenderer.Convention.HFR,"Main");
  // Transparent glass must read the actual Main/Fluid surfaces,not invisible proxy-cell cubes.
  if(buffers instanceof MultiBufferSource.BufferSource source)source.endBatch(mainType);
  if(machine.tank.getFill()>0){pose.pushPose();pose.translate(0,1.25,0);pose.scale(1,(float)((double)machine.tank.getFill()/machine.tank.getMaxFill()),1);pose.translate(0,-1.25,0);LegacyObjRenderer.renderOnly(mesh,pose,SolidifierRenderTypes.colorOnly(buffers.getBuffer(SolidifierRenderTypes.FLUID)),light,overlay,0xff000000|machine.tank.getTankType().getColor()&0xffffff,LegacyObjRenderer.Convention.HFR,"Fluid");pose.popPose();if(buffers instanceof MultiBufferSource.BufferSource source)source.endBatch(SolidifierRenderTypes.FLUID);}
  LegacyObjRenderer.renderOnly(mesh,pose,SolidifierRenderTypes.colorOnly(buffers.getBuffer(SolidifierRenderTypes.GLASS)),light,overlay,0x26bfffff,LegacyObjRenderer.Convention.HFR,"Glass");pose.popPose();
 }
 @Override public boolean shouldRenderOffScreen(SolidifierEntity e){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(SolidifierEntity e){var p=e.getBlockPos();return new AABB(p.getX()-1,p.getY(),p.getZ()-1,p.getX()+2,p.getY()+4,p.getZ()+2);}
}
