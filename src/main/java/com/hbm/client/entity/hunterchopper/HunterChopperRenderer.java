package com.hbm.client.entity.hunterchopper;
import com.hbm.client.model.legacy.LegacyModelRenderContext;
import com.hbm.entity.mob.hunterchopper.HunterChopper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
/** Actual source RenderHunterChopper matrices and unchanged ModelHunterChopper box/clock body. */
public final class HunterChopperRenderer extends EntityRenderer<HunterChopper> {
 private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/entity/chopper.png");private final ModelHunterChopper model=new ModelHunterChopper();public HunterChopperRenderer(EntityRendererProvider.Context context){super(context);}
 @Override public ResourceLocation getTextureLocation(HunterChopper entity){return TEXTURE;}@Override public boolean shouldRender(HunterChopper entity,Frustum camera,double x,double y,double z){return entity.shouldRender(x,y,z);}
 @Override public void render(HunterChopper entity,float yaw,float partial,PoseStack pose,MultiBufferSource buffers,int light){pose.pushPose();pose.translate(0,2.75,0);pose.scale(4,4,4);pose.mulPose(Axis.XP.rotationDegrees(180));pose.mulPose(Axis.YP.rotationDegrees(entity.yRotO+(entity.getYRot()-entity.yRotO)*partial-90));pose.mulPose(Axis.ZP.rotationDegrees(entity.xRotO+(entity.getXRot()-entity.xRotO)*partial));try(var scope=LegacyModelRenderContext.push(pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,OverlayTexture.NO_OVERLAY,-1)){model.renderAll(.0625F);}pose.popPose();}
}
