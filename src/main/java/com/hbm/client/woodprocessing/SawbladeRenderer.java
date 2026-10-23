package com.hbm.client.woodprocessing;
import com.hbm.machine.woodprocessing.SawbladeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
public final class SawbladeRenderer extends EntityRenderer<SawbladeEntity> {
    public SawbladeRenderer(EntityRendererProvider.Context context){super(context);shadowRadius=0;}
    @Override public ResourceLocation getTextureLocation(SawbladeEntity blade){return SawmillRenderer.TEXTURE;}
    @Override public void render(SawbladeEntity blade,float yaw,float partial,PoseStack pose,MultiBufferSource buffers,int light){pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees(switch(blade.orientation()%6){case 5->90;case 2->180;case 4->270;default->0;}));pose.translate(0,0,-1);if(blade.orientation()<6)pose.mulPose(Axis.ZN.rotationDegrees((float)(System.currentTimeMillis()%1800/3D)));pose.translate(0,-1.375,0);SawmillRenderer.part("Blade",pose,buffers,light,OverlayTexture.NO_OVERLAY);pose.popPose();}
}
