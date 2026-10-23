package com.hbm.client.entity.crab;
import com.hbm.client.model.legacy.*;
import com.hbm.client.model.legacy.generated.ModelBullet;
import com.hbm.entity.mob.crab.CyberTauBullet;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Random;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
/** Real source RenderRocket used for old EntityBullet, preserving its8x4 ModelBullet and seeded rotation. */
public final class CyberTauRenderer extends EntityRenderer<CyberTauBullet>{
 private final ModelBullet model=new ModelBullet();public CyberTauRenderer(EntityRendererProvider.Context context){super(context);}
 @Override public ResourceLocation getTextureLocation(CyberTauBullet bullet){return ResourceLocation.parse(bullet.chopper()?"hbm:textures/models/emplacer.png":bullet.critical()?"hbm:textures/models/tau.png":"hbm:textures/models/bullet.png");}
 @Override public void render(CyberTauBullet bullet,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){poses.pushPose();poses.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partial,bullet.yRotO,bullet.getYRot())-90));poses.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partial,bullet.xRotO,bullet.getXRot())+180));poses.scale(1.5F,1.5F,1.5F);poses.mulPose(Axis.XP.rotationDegrees(new Random(bullet.getId()).nextInt(360)));try(var scope=LegacyModelRenderContext.push(poses,buffers.getBuffer(RenderType.entityCutout(getTextureLocation(bullet))),bullet.critical()||bullet.chopper()?LightTexture.FULL_BRIGHT:light,OverlayTexture.NO_OVERLAY,-1)){model.renderAll(.0625F);}poses.popPose();super.render(bullet,yaw,partial,poses,buffers,light);}
}
