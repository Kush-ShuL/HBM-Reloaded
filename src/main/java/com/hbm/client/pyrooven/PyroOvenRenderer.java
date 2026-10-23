package com.hbm.client.pyrooven;
import com.hbm.client.model.*;
import com.hbm.machine.pyrooven.PyroOvenEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Exact Oven/Slider/Fan meshes, source soft-peak translation and45deg/tick pivoted fan. */
public final class PyroOvenRenderer implements BlockEntityRenderer<PyroOvenEntity> {
 static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/pyrooven.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/pyrooven.png");
 public PyroOvenRenderer(BlockEntityRendererProvider.Context context){}
 static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
 static void inventory(PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(model(),poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);}
 @Override public void render(PyroOvenEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){float yaw=switch(DummyableBlock.facing(machine.getBlockState())){case NORTH->180;case WEST->270;case EAST->90;default->0;};var mesh=model();LegacyContactShadows.render(machine,mesh,new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)),poses,buffers);poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(yaw));var vertices=buffers.getBuffer(RenderType.entityCutout(TEXTURE));float anim=machine.prevAnim+(machine.anim-machine.prevAnim)*partial;
  LegacyObjRenderer.renderOnly(mesh,poses,vertices,light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,"Oven");poses.pushPose();poses.translate(Math.sin(Math.PI*.5*Math.cos(anim*.125))*.5-.5,0,0);LegacyObjRenderer.renderOnly(mesh,poses,vertices,light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,"Slider");poses.popPose();poses.pushPose();poses.translate(1.5,0,1.5);poses.mulPose(Axis.YP.rotationDegrees((float)(anim*45D%360D)));poses.translate(-1.5,0,-1.5);LegacyObjRenderer.renderOnly(mesh,poses,vertices,light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,"Fan");poses.popPose();poses.popPose();
 }
 @Override public boolean shouldRenderOffScreen(PyroOvenEntity machine){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(PyroOvenEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-3,p.getY(),p.getZ()-3,p.getX()+4,p.getY()+3.5,p.getZ()+4);}
}
