package com.hbm.client.satellite.attack;
import com.hbm.satellite.attack.DeathBlastEntity;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
/** Source60tick shrinking sphere plus8 cumulative additive shells and the green/magenta250high beam. */
public final class DeathBlastRenderer extends EntityRenderer<DeathBlastEntity> {
 private static final ResourceLocation SPHERE=ResourceLocation.parse("hbm:models/satellite/attack112/sphere.obj");
 public DeathBlastRenderer(EntityRendererProvider.Context context){super(context);}
 @Override public void render(DeathBlastEntity entity,float yaw,float partial,PoseStack pose,MultiBufferSource buffers,int light){
  AttackBeamGeometry.render(pose,buffers,0xff00ff00,0xffff00ff);
  double age=(entity.tickCount+partial)/(double)DeathBlastEntity.MAX_AGE,scale=Math.max(0,10-10*age);int alpha=Math.clamp((int)(255*age),0,255);
  var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),SPHERE);
  pose.pushPose();pose.scale((float)scale,(float)scale,(float)scale);
  LegacyObjRenderer.renderAll(mesh,pose,AttackRenderTypes.colorOnly(buffers.getBuffer(AttackRenderTypes.ORB)),LightTexture.FULL_BRIGHT,0,alpha<<24|0x0dff0d,LegacyObjRenderer.Convention.FORGE);
  pose.scale(1.25F,1.25F,1.25F);int halo=Math.clamp((int)(255*age*.125),0,255)<<24|0x00ff00;
  for(int i=0;i<8;i++){LegacyObjRenderer.renderAll(mesh,pose,AttackRenderTypes.colorOnly(buffers.getBuffer(AttackRenderTypes.GLOW)),LightTexture.FULL_BRIGHT,0,halo,LegacyObjRenderer.Convention.FORGE);pose.scale(1.05F,1.05F,1.05F);}pose.popPose();
 }
 @Override public ResourceLocation getTextureLocation(DeathBlastEntity entity){return null;}
}
