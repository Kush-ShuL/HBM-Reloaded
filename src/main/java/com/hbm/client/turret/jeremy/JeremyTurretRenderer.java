package com.hbm.client.turret.jeremy;
import com.hbm.client.model.*;
import com.hbm.client.turret.rotary.RotaryTurretRenderer;
import com.hbm.machine.turret.jeremy.JeremyTurretEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
/** Source RenderTurretJeremy: Chekhov base/carriage, source yaw-90 and Z pitch pivot at 1.5, separate Gun mesh. */
public final class JeremyTurretRenderer implements BlockEntityRenderer<JeremyTurretEntity> {
 private static final ResourceLocation BASE=ResourceLocation.parse("hbm:models/turrets/turret_chekhov.obj"),MODEL=ResourceLocation.parse("hbm:models/turrets/turret_jeremy.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/turrets/jeremy.png");
 public JeremyTurretRenderer(BlockEntityRendererProvider.Context context){}
 @Override public void render(JeremyTurretEntity turret,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){var manager=Minecraft.getInstance().getResourceManager();var base=LegacyObjModels.INSTANCE.get(manager,BASE);var offset=turret.horizontalOffset();LegacyContactShadows.render(turret,RotaryTurretRenderer.shadow(base),new org.joml.Matrix4f().translation((float)offset.x,0,(float)offset.z),pose,buffers);pose.pushPose();pose.translate(offset.x,0,offset.z);RotaryTurretRenderer.connectors(turret,base,pose,buffers,light,overlay);RotaryTurretRenderer.part(base,pose,buffers,light,overlay,"base","Base");pose.mulPose(Axis.YP.rotationDegrees((float)-Math.toDegrees(Mth.lerp(partial,turret.lastRotationYaw,turret.rotationYaw))-90));RotaryTurretRenderer.part(base,pose,buffers,light,overlay,"carriage","Carriage");pose.translate(0,1.5,0);pose.mulPose(Axis.ZP.rotationDegrees((float)Math.toDegrees(Mth.lerp(partial,turret.lastRotationPitch,turret.rotationPitch))));pose.translate(0,-1.5,0);LegacyObjRenderer.renderOnly(LegacyObjModels.INSTANCE.get(manager,MODEL),pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR,"Gun");pose.popPose();}
 @Override public boolean shouldRenderOffScreen(JeremyTurretEntity turret){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(JeremyTurretEntity turret){return new AABB(Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY);}
}
