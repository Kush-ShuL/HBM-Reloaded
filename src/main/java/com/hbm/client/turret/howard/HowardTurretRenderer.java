package com.hbm.client.turret.howard;
import com.hbm.client.model.*;
import com.hbm.client.turret.rotary.RotaryTurretRenderer;
import com.hbm.machine.turret.howard.HowardTurretEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
/** Original normal and damaged CIWS renderers: independent opposite barrel spins, damaged bottom barrel fixed. */
public final class HowardTurretRenderer implements BlockEntityRenderer<HowardTurretEntity> {
 private static final ResourceLocation BASE=ResourceLocation.parse("hbm:models/turrets/turret_chekhov.obj"),NORMAL=ResourceLocation.parse("hbm:models/turrets/turret_howard.obj"),DAMAGED=ResourceLocation.parse("hbm:models/turrets/turret_howard_damaged.obj");
 public HowardTurretRenderer(BlockEntityRendererProvider.Context context){}
 @Override public void render(HowardTurretEntity turret,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){boolean damaged=turret.kind.damaged;var resources=Minecraft.getInstance().getResourceManager();var base=LegacyObjModels.INSTANCE.get(resources,BASE);var mesh=LegacyObjModels.INSTANCE.get(resources,damaged?DAMAGED:NORMAL);var offset=turret.horizontalOffset();String prefix=damaged?"rusted/":"";LegacyContactShadows.render(turret,RotaryTurretRenderer.shadow(base),new org.joml.Matrix4f().translation((float)offset.x,0,(float)offset.z),pose,buffers);pose.pushPose();pose.translate(offset.x,0,offset.z);if(!damaged)RotaryTurretRenderer.connectors(turret,base,pose,buffers,light,overlay);RotaryTurretRenderer.part(base,pose,buffers,light,overlay,prefix+"base","Base");pose.mulPose(Axis.YP.rotationDegrees((float)-Math.toDegrees(Mth.lerp(partial,turret.lastRotationYaw,turret.rotationYaw))-90));RotaryTurretRenderer.part(mesh,pose,buffers,light,overlay,prefix+"carriage_ciws","Carriage");pose.translate(0,2.25,0);pose.mulPose(Axis.ZP.rotationDegrees((float)Math.toDegrees(Mth.lerp(partial,turret.lastRotationPitch,turret.rotationPitch))));pose.translate(0,-2.25,0);RotaryTurretRenderer.part(mesh,pose,buffers,light,overlay,prefix+"howard","Body");float spin=Mth.lerp(partial,turret.lastSpin,turret.spin);pose.pushPose();pose.translate(0,2.5,0);pose.mulPose(Axis.XP.rotationDegrees(-spin));pose.translate(0,-2.5,0);RotaryTurretRenderer.part(mesh,pose,buffers,light,overlay,prefix+"howard_barrels","BarrelsTop");pose.popPose();if(!damaged){pose.translate(0,2,0);pose.mulPose(Axis.XP.rotationDegrees(spin));pose.translate(0,-2,0);}RotaryTurretRenderer.part(mesh,pose,buffers,light,overlay,prefix+"howard_barrels","BarrelsBottom");pose.popPose();}
 @Override public boolean shouldRenderOffScreen(HowardTurretEntity turret){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(HowardTurretEntity turret){return new AABB(Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY);}
}
