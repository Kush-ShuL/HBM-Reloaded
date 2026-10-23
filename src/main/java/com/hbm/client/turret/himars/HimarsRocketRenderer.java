package com.hbm.client.turret.himars;
import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.client.bomb.nuclear.LegacyAbmContrailParticle;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.machine.turret.himars.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
/** Exact RenderArtilleryRocket rotations and RocketStandard/Single group, followed by the original render-distance contrail sampler. */
public final class HimarsRocketRenderer extends EntityRenderer<HimarsRocketEntity> {
 private static final class Trail {double x,y,z,path;}
 private final Map<HimarsRocketEntity,Trail> trails=new WeakHashMap<>();
 public HimarsRocketRenderer(EntityRendererProvider.Context context){super(context);shadowRadius=0;}
 @Override public ResourceLocation getTextureLocation(HimarsRocketEntity rocket){return HimarsRocketTypes.type(rocket.rocketType()).texture();}
 @Override public boolean shouldRender(HimarsRocketEntity rocket,Frustum frustum,double x,double y,double z){return true;}
 @Override public void render(HimarsRocketEntity rocket,float yaw,float partial,PoseStack pose,MultiBufferSource buffers,int light){var type=HimarsRocketTypes.type(rocket.rocketType());pose.pushPose();try{pose.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partial,rocket.yRotO,rocket.getYRot())-90));pose.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partial,rocket.xRotO,rocket.getXRot())-90));pose.mulPose(Axis.YP.rotationDegrees(90));pose.mulPose(Axis.XP.rotationDegrees(90));LegacyObjRenderer.renderOnly(HimarsTurretRenderer.mesh(HimarsTurretRenderer.MODEL),pose,buffers.getBuffer(HimarsRocketRenderTypes.type(type.texture())),light,OverlayTexture.NO_OVERLAY,-1,LegacyObjRenderer.Convention.HFR,type.modelType()==0?"RocketStandard":"RocketSingle");trail(rocket,partial);}finally{pose.popPose();}}
 private void trail(HimarsRocketEntity rocket,float partial){if(!(rocket.level() instanceof ClientLevel level))return;double x=Mth.lerp(partial,rocket.xOld,rocket.getX()),y=Mth.lerp(partial,rocket.yOld,rocket.getY()),z=Mth.lerp(partial,rocket.zOld,rocket.getZ());var state=trails.computeIfAbsent(rocket,unused->new Trail());if(state.x==0)state.x=x;if(state.y==0)state.y=y;if(state.z==0)state.z=z;var delta=LegacyVector.createVectorHelper(state.x-rocket.getX(),state.y-rocket.getY(),state.z-rocket.getZ());state.path+=delta.lengthVector();var unit=delta.normalize();for(;state.path>=4;state.path-=4)Minecraft.getInstance().particleEngine.add(new LegacyAbmContrailParticle(level,x-unit.xCoord*state.path,y-unit.yCoord*state.path,z-unit.zCoord*state.path));state.x=x;state.y=y;state.z=z;}
}
