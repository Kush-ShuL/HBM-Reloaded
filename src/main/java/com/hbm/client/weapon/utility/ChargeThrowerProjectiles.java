package com.hbm.client.weapon.utility;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import com.hbm.client.weapon.pepperbox.GunBulletRenderContext;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.weapon.sedna.gun.utility.ItemGunChargeThrower;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
/** Original LegoClient CT mesh transforms and ten-segment, two-crossed-strip tether. */
public final class ChargeThrowerProjectiles {
 private static final ResourceLocation WIRE=ResourceLocation.parse("hbm:textures/models/network/wire_greyscale.png");
 public static void hook(EntityBulletBaseMK4 bullet,float partial){
  var context=GunBulletRenderContext.current();var poses=context.poses();poses.pushPose();
  try(var scope=LegacyGunGL.push(poses,context.buffers(),context.light(),OverlayTexture.NO_OVERLAY)){
   poses.mulPose(Axis.YP.rotationDegrees(bullet.yRotO+(bullet.getYRot()-bullet.yRotO)*partial-90));poses.mulPose(Axis.ZP.rotationDegrees(bullet.xRotO+(bullet.getXRot()-bullet.xRotO)*partial+180));
   transform(poses);LegacyGunGL.texture(ChargeThrowerModels.charge_thrower_hook_tex);ChargeThrowerModels.charge_thrower.renderPart("Hook");
  }catch(RuntimeException|Error failure){throw failure;}catch(Exception impossible){throw new AssertionError(impossible);}finally{poses.popPose();}
  if(bullet.getThrower() instanceof Player player&&player.getMainHandItem().getItem() instanceof ItemGunChargeThrower&&ItemGunChargeThrower.getLastHook(player.getMainHandItem())==bullet.getId())wire(bullet,partial,context);
 }
 public static void mortar(EntityBulletBaseMK4 bullet,float partial,boolean heavy){
  var context=GunBulletRenderContext.current();var poses=context.poses();poses.pushPose();
  try(var scope=LegacyGunGL.push(poses,context.buffers(),context.light(),OverlayTexture.NO_OVERLAY)){
   // The sole MK4 renderer already applies the configured yaw/pitch before this callback.
   transform(poses);LegacyGunGL.texture(ChargeThrowerModels.charge_thrower_mortar_tex);ChargeThrowerModels.charge_thrower.renderPart("Mortar");if(heavy)ChargeThrowerModels.charge_thrower.renderPart("Oomph");
  }catch(RuntimeException|Error failure){throw failure;}catch(Exception impossible){throw new AssertionError(impossible);}finally{poses.popPose();}
 }
 private static void transform(PoseStack poses){poses.scale(.125F,.125F,.125F);poses.mulPose(Axis.YN.rotationDegrees(90));poses.mulPose(Axis.ZP.rotationDegrees(180));poses.translate(0,0,-6);}
 private static double lerp(double a,double b,float partial){return a+(b-a)*partial;}
 private static void wire(EntityBulletBaseMK4 bullet,float partial,GunBulletRenderContext context){
  double bx=lerp(bullet.xo,bullet.getX(),partial),by=lerp(bullet.yo,bullet.getY(),partial),bz=lerp(bullet.zo,bullet.getZ(),partial);var owner=bullet.getThrower();
  double x=lerp(owner.xo,owner.getX(),partial),y=lerp(owner.yo,owner.getY(),partial),z=lerp(owner.zo,owner.getZ(),partial);
  double yaw=lerp(owner.yRotO,owner.getYRot(),partial),pitch=lerp(owner.xRotO,owner.getXRot(),partial);
  var offset=new Vec3(.125,.25,-.75).xRot((float)-pitch/180F*(float)Math.PI).yRot((float)-yaw/180F*(float)Math.PI);
  var delta=new Vec3(x-offset.x-bx,y+owner.getEyeHeight()-offset.y-by,z-offset.z-bz);
  // text's native vertex-color/lightmap shader is deliberately unlit and uncullled, as source GL_LIGHTING off.
  var out=context.buffers().getBuffer(RenderType.text(WIRE));double hang=Math.min((double)(float)Math.sqrt(delta.lengthSqr())/15,.5),girth=.03125;
  double horizontal=Math.sqrt(delta.x*delta.x+delta.z*delta.z),aYaw=Math.atan2(delta.x,delta.z),aPitch=Math.atan2(delta.y,horizontal),turn=Math.PI*.5;
  double iZ=Math.cos(aYaw)*Math.cos(aPitch+turn)*girth,iX=Math.sin(aYaw)*Math.cos(aPitch+turn)*girth,iY=Math.sin(aPitch+turn)*girth,jZ=Math.cos(aYaw+turn)*girth,jX=Math.sin(aYaw+turn)*girth;
  for(float j=0;j<10;j++){
   float k=j+1;double sagJ=Math.sin(j/10*Math.PI)*hang,sagK=Math.sin(k/10*Math.PI)*hang,sagMean=(sagJ+sagK)/2,mid=j+.5;
   int light=LevelRenderer.getLightColor(bullet.level(),BlockPos.containing(bx+delta.x/10*mid,by+delta.y/10*mid-sagMean,bz+delta.z/10*mid));
   segment(out,context.poses(),delta.x*j/10,delta.y*j/10-sagJ,delta.z*j/10,delta.x*k/10,delta.y*k/10-sagK,delta.z*k/10,iX,iY,iZ,jX,jZ,light);
  }
 }
 private static void segment(VertexConsumer out,PoseStack poses,double x,double y,double z,double a,double b,double c,double iX,double iY,double iZ,double jX,double jZ,int light){
  double dx=a-x,dy=b-y,dz=c-z;int wrap=(int)Math.ceil(Math.sqrt(dx*dx+dy*dy+dz*dz)*8);if(dx+dz<0){wrap*=-1;jZ*=-1;jX*=-1;}
  vertex(out,poses,x+iX,y+iY,z+iZ,0,0,light);vertex(out,poses,x-iX,y-iY,z-iZ,0,1,light);vertex(out,poses,a-iX,b-iY,c-iZ,wrap,1,light);vertex(out,poses,a+iX,b+iY,c+iZ,wrap,0,light);
  vertex(out,poses,x+jX,y,z+jZ,0,0,light);vertex(out,poses,x-jX,y,z-jZ,0,1,light);vertex(out,poses,a-jX,b,c-jZ,wrap,1,light);vertex(out,poses,a+jX,b,c+jZ,wrap,0,light);
 }
 private static void vertex(VertexConsumer out,PoseStack poses,double x,double y,double z,float u,float v,int light){out.addVertex(poses.last(),(float)x,(float)y,(float)z).setColor(96,96,96,255).setUv(u,v).setLight(light);}
 private ChargeThrowerProjectiles(){}
}
