package com.hbm.client.entity.aircraft.bomber;
import com.hbm.client.model.*;
import com.hbm.entity.aircraft.bomber.EntityBombletZeta;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
/** Original RenderBombletTheta's real Zeta branch, source geometry and source skin. */
public final class BombletZetaRenderer extends EntityRenderer<EntityBombletZeta> {
 private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/bomblettheta.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/bombletzetatexture.png");
 public BombletZetaRenderer(EntityRendererProvider.Context context){super(context);}
 @Override public ResourceLocation getTextureLocation(EntityBombletZeta entity){return TEXTURE;}
 @Override public boolean shouldRender(EntityBombletZeta entity,Frustum frustum,double x,double y,double z){return entity.shouldRenderAtSqrDistance(entity.distanceToSqr(x,y,z));}
 @Override public void render(EntityBombletZeta entity,float yaw,float partial,PoseStack pose,MultiBufferSource buffers,int light){
  pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees(entity.yRotO+(entity.getYRot()-entity.yRotO)*partial-90));pose.mulPose(Axis.ZP.rotationDegrees(entity.xRotO+(entity.getXRot()-entity.xRotO)*partial));pose.scale(.5F,.5F,.5F);
  var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);LegacyObjRenderer.renderAll(mesh,pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,OverlayTexture.NO_OVERLAY,-1,LegacyObjRenderer.Convention.FORGE);pose.popPose();
 }
}
