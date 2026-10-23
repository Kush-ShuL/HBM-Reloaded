package com.hbm.client.largeturbine;
import com.hbm.client.model.*;
import com.hbm.machine.largeturbine.LargeTurbineEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Original RenderBigTurbine: initial90Y+sourcefacing,Tz-1,rotor pivot(0,1,0) about positiveZ. */
public final class LargeTurbineRenderer implements BlockEntityRenderer<LargeTurbineEntity> {
 private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/turbine.obj"),BODY=ResourceLocation.parse("hbm:textures/models/machines/turbine.png"),BLADES=ResourceLocation.parse("hbm:textures/models/turbofan_blades.png");
 public LargeTurbineRenderer(BlockEntityRendererProvider.Context context){}static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
 static void part(PoseStack pose,MultiBufferSource buffers,int light,int overlay,String part,boolean flat){LegacyObjRenderer.renderOnly(model(),pose,buffers.getBuffer(RenderType.entityCutoutNoCull(part.equals("Body")?BODY:BLADES)),light,overlay,-1,flat?LegacyObjRenderer.Convention.HFR_FLAT:LegacyObjRenderer.Convention.HFR,part);}
 static void inventory(PoseStack pose,MultiBufferSource buffers,int light,int overlay){pose.mulPose(Axis.YP.rotationDegrees(90));part(pose,buffers,light,overlay,"Body",false);part(pose,buffers,light,overlay,"Blades",false);}
 private static int yaw(Direction direction){return 90+switch(direction){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};}
 @Override public void render(LargeTurbineEntity turbine,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){int yaw=yaw(DummyableBlock.facing(turbine.getBlockState()));var transform=new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)).translate(0,0,-1);LegacyContactShadows.render(turbine,model(),transform,pose,buffers);pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(yaw));pose.translate(0,0,-1);part(pose,buffers,light,overlay,"Body",false);pose.translate(0,1,0);pose.mulPose(Axis.ZP.rotationDegrees(turbine.lastRotor+(turbine.rotor-turbine.lastRotor)*partial));pose.translate(0,-1,0);part(pose,buffers,light,overlay,"Blades",true);pose.popPose();}
 @Override public boolean shouldRenderOffScreen(LargeTurbineEntity turbine){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(LargeTurbineEntity turbine){return AABB.INFINITE;}
}
