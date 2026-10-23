package com.hbm.client.bomb.conventional.grenade;

import com.hbm.bomb.conventional.grenade.UniversalGrenade;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
/** Original RenderGrenadeUniversal: interpolation, source spin and thrown shell-specific part omissions. */
public final class UniversalGrenadeRenderer extends EntityRenderer<UniversalGrenade>{
 public UniversalGrenadeRenderer(EntityRendererProvider.Context context){super(context);}
 @Override public ResourceLocation getTextureLocation(UniversalGrenade grenade){return ResourceLocation.parse("hbm:textures/models/grenades/frag.png");}
 @Override public void render(UniversalGrenade grenade,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){poses.pushPose();poses.scale(.0625F,.0625F,.0625F);poses.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partial,grenade.yRotO,grenade.getYRot())));poses.mulPose(Axis.XP.rotationDegrees((float)(grenade.previousSpin+(grenade.spin-grenade.previousSpin)*partial)));if(grenade.bounces()>0)poses.mulPose(Axis.ZP.rotationDegrees(-80));UniversalGrenadeItemRenderer.grenade(grenade.grenade(),null,poses,buffers,light,OverlayTexture.NO_OVERLAY);poses.popPose();super.render(grenade,yaw,partial,poses,buffers,light);}
}
