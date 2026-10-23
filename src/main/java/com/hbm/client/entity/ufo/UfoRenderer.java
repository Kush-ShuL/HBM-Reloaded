package com.hbm.client.entity.ufo;
import com.hbm.client.model.*;
import com.hbm.entity.mob.ufo.EntityUFO;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
/** Unchanged RenderUFO: y+1, normalized diagonal death tilt, 5deg/tick spin, scale2, authored smooth HFR mesh/UV. */
public final class UfoRenderer extends EntityRenderer<EntityUFO> {
 private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/entity/ufo.png"),MODEL=ResourceLocation.parse("hbm:models/mobs/ufo.obj");
 public UfoRenderer(EntityRendererProvider.Context context){super(context);}
 @Override public ResourceLocation getTextureLocation(EntityUFO entity){return TEXTURE;}
 @Override public void render(EntityUFO entity,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){
  poses.pushPose();poses.translate(0,1,0);
  if(!entity.isAlive())poses.mulPose(new Quaternionf().rotationAxis((float)Math.toRadians(entity.deathTime+30+partial),new Vector3f(1,0,1).normalize()));
  poses.pushPose();double rotation=(entity.tickCount+partial)*5%360D;poses.mulPose(Axis.YP.rotationDegrees((float)rotation));poses.scale(2,2,2);
  var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);LegacyObjRenderer.render(mesh,poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,OverlayTexture.NO_OVERLAY,0xffffffff,LegacyObjRenderer.Convention.HFR,face->true);poses.popPose();
  if(entity.getBeam()){double length=entity.getY()-entity.beamFloor();if(length>0){var skeleton=new Vec3(0,-length,0);UfoBeamRenderer.beam(skeleton,poses,buffers,false,0x101020,0,(int)(length+1),0,6,1.5F);UfoBeamRenderer.beam(skeleton,poses,buffers,true,0x202060,entity.tickCount/2,(int)(length/2+1),3,2,.0625F);UfoBeamRenderer.beam(skeleton,poses,buffers,true,0x202060,entity.tickCount/4,(int)(length/2+1),3,2,.0625F);}}
  poses.popPose();
 }
}
