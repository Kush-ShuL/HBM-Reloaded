package com.hbm.client.turbofan;
import com.hbm.client.model.*;
import com.hbm.machine.turbofan.TurbofanEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** RenderTurbofan's exact Body/Blades/Afterburner and negativeZ pivot, all smooth/cull. */
public final class TurbofanRenderer implements BlockEntityRenderer<TurbofanEntity> {
 private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/turbofan.obj"),BODY=ResourceLocation.parse("hbm:textures/models/machines/turbofan.png"),BACK=ResourceLocation.parse("hbm:textures/models/machines/turbofan_back.png"),AFTER=ResourceLocation.parse("hbm:textures/models/machines/turbofan_afterburner.png");
 public TurbofanRenderer(BlockEntityRendererProvider.Context context){}private static WavefrontMesh mesh(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}private static void part(PoseStack pose,MultiBufferSource buffers,int light,int overlay,ResourceLocation texture,String part){LegacyObjRenderer.renderOnly(mesh(),pose,buffers.getBuffer(RenderType.entitySolid(texture)),light,overlay,-1,LegacyObjRenderer.Convention.HFR,part);}
 static void inventory(PoseStack pose,MultiBufferSource buffers,int light,int overlay){part(pose,buffers,light,overlay,BODY,"Body");part(pose,buffers,light,overlay,BODY,"Blades");part(pose,buffers,light,overlay,BACK,"Afterburner");}private static int yaw(Direction d){return switch(d){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};}
 @Override public void render(TurbofanEntity t,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){int yaw=yaw(DummyableBlock.facing(t.getBlockState()));var transform=new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw));LegacyContactShadows.render(t,mesh(),transform,pose,buffers);pose.pushPose();pose.mulPose(transform);part(pose,buffers,light,overlay,BODY,"Body");pose.pushPose();pose.translate(0,1.5,0);pose.mulPose(Axis.ZN.rotationDegrees(t.lastSpin+(t.spin-t.lastSpin)*partial));pose.translate(0,-1.5,0);part(pose,buffers,light,overlay,BODY,"Blades");pose.popPose();part(pose,buffers,light,overlay,t.afterburner==0?BACK:AFTER,"Afterburner");pose.popPose();}
 @Override public boolean shouldRenderOffScreen(TurbofanEntity t){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(TurbofanEntity t){return AABB.INFINITE;}
}
