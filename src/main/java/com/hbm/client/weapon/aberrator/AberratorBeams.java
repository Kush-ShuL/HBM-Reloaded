package com.hbm.client.weapon.aberrator;
import com.hbm.client.weapon.SednaBeamRenderContext;
import com.hbm.client.weapon.pepperbox.*;
import com.hbm.entity.projectile.EntityBulletBeamBase;
import com.mojang.math.Axis;
import net.minecraft.util.Mth;
/** Exact source LegoClient RENDER_CRACKLE/RENDER_BLACK_LIGHTNING, reusing the unique original tapered 24-vertex bullet primitive. */
public final class AberratorBeams {
 public static void draw(EntityBulletBeamBase bullet,float partial,boolean black){var context=SednaBeamRenderContext.current();var poses=context.poses();poses.pushPose();try{poses.mulPose(Axis.YP.rotationDegrees(180-bullet.getYRot()));poses.mulPose(Axis.XP.rotationDegrees(-bullet.getXRot()-90));double age=Mth.clamp(1D-((double)bullet.tickCount-2+partial)/bullet.getBulletConfig().expires,0,1);poses.scale((float)(age*5),1,(float)(age*5));poses.translate(0,bullet.beamLength,0);poses.mulPose(Axis.ZP.rotationDegrees(-90));
  GunBulletRenderContext.begin(poses,context.buffers(),context.light());try{GunBulletRenderer.renderBulletStandard(black?0x4C3093:0xE3D692,black?0x000000:0xffffff,bullet.beamLength,.03125D,.03125D*.25D,true);}finally{GunBulletRenderContext.end();}
 }finally{poses.popPose();}}
 private AberratorBeams(){}
}
