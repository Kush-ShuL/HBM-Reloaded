package com.hbm.client.entity.botprime;
import com.hbm.client.model.*;
import com.hbm.entity.mob.botprime.BotPrimeBase;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
/** Original head/body HFRsmooth complete meshes with exact Y(yaw-90)/Z(pitch-90),no cull and no ground shadow. */
public final class BotPrimeRenderer<T extends BotPrimeBase> extends EntityRenderer<T> {
 private final ResourceLocation model,skin;
 public BotPrimeRenderer(EntityRendererProvider.Context context,boolean head){super(context);model=ResourceLocation.parse("hbm:models/mobs/bot_prime_"+(head?"head":"body")+".obj");skin=ResourceLocation.parse("hbm:textures/entity/mark_zero_"+(head?"head":"body")+".png");shadowRadius=0;shadowStrength=0;}
 @Override public void render(T entity,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){poses.pushPose();poses.mulPose(Axis.YP.rotationDegrees(entity.yRotO+(entity.getYRot()-entity.yRotO)*partial-90));poses.mulPose(Axis.ZP.rotationDegrees(entity.xRotO+(entity.getXRot()-entity.xRotO)*partial-90));LegacyObjRenderer.renderAll(LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),model),poses,buffers.getBuffer(RenderType.entityCutoutNoCull(skin)),light,net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,0xffffffff,LegacyObjRenderer.Convention.HFR);poses.popPose();}
 @Override public ResourceLocation getTextureLocation(T entity){return skin;}
}
