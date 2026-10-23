package com.hbm.client.turret.himars;
import com.hbm.client.model.*;
import com.hbm.client.turret.rotary.RotaryTurretRenderer;
import com.hbm.machine.turret.himars.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
/** Exact RenderTurretHIMARS: separate arty Base, yaw Carriage, pitch around(0,2.25,2), crane Z-5 and actual remaining pod caps. */
public final class HimarsTurretRenderer implements BlockEntityRenderer<HimarsTurretEntity> {
 static final ResourceLocation ARTY=HimarsTurretContent.id("models/turrets/turret_arty.obj"),MODEL=HimarsTurretContent.id("models/turrets/turret_himars.obj"),ARTY_TEX=HimarsTurretContent.id("textures/models/turrets/arty.png"),TEXTURE=HimarsTurretContent.id("textures/models/turrets/himars.png");
 public HimarsTurretRenderer(BlockEntityRendererProvider.Context context){}
 static WavefrontMesh mesh(ResourceLocation id){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),id);}
 static void part(ResourceLocation model,ResourceLocation texture,PoseStack pose,MultiBufferSource buffers,int light,int overlay,String...parts){LegacyObjRenderer.renderOnly(mesh(model),pose,buffers.getBuffer(RenderType.entityCutout(texture)),light,overlay,-1,LegacyObjRenderer.Convention.HFR,parts);}
 static void item(PoseStack pose,MultiBufferSource buffers,int light,int overlay){pose.mulPose(Axis.YP.rotationDegrees(-90));pose.scale(.5F,.5F,.5F);part(ARTY,ARTY_TEX,pose,buffers,light,overlay,"Base");part(MODEL,TEXTURE,pose,buffers,light,overlay,"Carriage","Launcher","Crane");part(MODEL,HimarsRocketTypes.type(0).texture(),pose,buffers,light,overlay,"TubeStandard");}
 @Override public void render(HimarsTurretEntity turret,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){var offset=turret.horizontalOffset();LegacyContactShadows.render(turret,RotaryTurretRenderer.shadow(mesh(ARTY)),new org.joml.Matrix4f().translation((float)offset.x,0,(float)offset.z),pose,buffers);pose.pushPose();try{pose.translate(offset.x,0,offset.z);part(ARTY,ARTY_TEX,pose,buffers,light,overlay,"Base");double yaw=-Math.toDegrees(Mth.lerp(partial,turret.lastRotationYaw,turret.rotationYaw))-90,pitch=Math.toDegrees(Mth.lerp(partial,turret.lastRotationPitch,turret.rotationPitch));pose.mulPose(Axis.YP.rotationDegrees((float)(yaw-90)));part(MODEL,TEXTURE,pose,buffers,light,overlay,"Carriage");pose.translate(0,2.25,2);pose.mulPose(Axis.XP.rotationDegrees((float)pitch));pose.translate(0,-2.25,-2);part(MODEL,TEXTURE,pose,buffers,light,overlay,"Launcher");pose.translate(0,0,Mth.lerp(partial,turret.lastCrane,turret.crane)*-5);part(MODEL,TEXTURE,pose,buffers,light,overlay,"Crane");if(turret.typeLoaded>=0){var type=HimarsRocketTypes.type(turret.typeLoaded);if(type.modelType()==0){part(MODEL,type.texture(),pose,buffers,light,overlay,"TubeStandard");for(int i=0;i<turret.ammo;i++)part(MODEL,type.texture(),pose,buffers,light,overlay,"CapStandard"+(5-i+1));}else{part(MODEL,type.texture(),pose,buffers,light,overlay,"TubeSingle");if(turret.hasAmmo())part(MODEL,type.texture(),pose,buffers,light,overlay,"CapSingle");}}}finally{pose.popPose();}}
 @Override public boolean shouldRenderOffScreen(HimarsTurretEntity turret){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(HimarsTurretEntity turret){return new AABB(Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY);}
}
