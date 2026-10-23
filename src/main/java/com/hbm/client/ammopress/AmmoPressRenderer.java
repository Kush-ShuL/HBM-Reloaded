package com.hbm.client.ammopress;
import com.hbm.client.model.*;
import com.hbm.machine.ammopress.AmmoPressEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
/** Original HFR smooth/no-cull Frame, Press, Shells and phase-dependent Bullets. */
public final class AmmoPressRenderer implements BlockEntityRenderer<AmmoPressEntity> {
 private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/ammo_press.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/ammo_press.png");
 public AmmoPressRenderer(BlockEntityRendererProvider.Context context){}
 static WavefrontMesh mesh(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
 static void part(String name,PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderOnly(mesh(),pose,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,name);}
 static void all(PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(mesh(),pose,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);}
 @Override public void render(AmmoPressEntity machine,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){float degrees=switch(DummyableBlock.facing(machine.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};LegacyContactShadows.render(machine,mesh(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(degrees)),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(degrees));part("Frame",pose,buffers,light,overlay);pose.pushPose();pose.translate(0,-Mth.lerp(partial,machine.prevPress,machine.press)*.25F,0);part("Press",pose,buffers,light,overlay);pose.popPose();pose.pushPose();pose.translate(0,Mth.lerp(partial,machine.prevLift,machine.lift)*.5F-.5F,0);part("Shells",pose,buffers,light,overlay);if(machine.animation==AmmoPressEntity.Animation.RETRACTING||machine.animation==AmmoPressEntity.Animation.LOWERING)part("Bullets",pose,buffers,light,overlay);pose.popPose();pose.popPose();}
 @Override public boolean shouldRenderOffScreen(AmmoPressEntity machine){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(AmmoPressEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-1,p.getY(),p.getZ()-1,p.getX()+2,p.getY()+2,p.getZ()+2);}
}
