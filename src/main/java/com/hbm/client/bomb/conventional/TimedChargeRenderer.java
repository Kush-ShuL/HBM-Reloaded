package com.hbm.client.bomb.conventional;

import com.hbm.bomb.conventional.TimedChargeBlock;
import com.hbm.bomb.conventional.TimedChargeBlockEntity;
import com.hbm.client.gui.font.LegacyFonts;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

/** The original separate green 00:00 text pass on RenderExplosiveCharge, with its six face transforms. */
public final class TimedChargeRenderer implements BlockEntityRenderer<TimedChargeBlockEntity> {
    public TimedChargeRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(TimedChargeBlockEntity charge,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        poses.pushPose();poses.translate(.5,.5,.5);
        switch(charge.getBlockState().getValue(TimedChargeBlock.FACE)){
            case DOWN->poses.mulPose(Axis.ZP.rotationDegrees(180));case UP->{}
            case NORTH->{poses.mulPose(Axis.YP.rotationDegrees(90));poses.mulPose(Axis.ZP.rotationDegrees(-90));}
            case SOUTH->{poses.mulPose(Axis.YP.rotationDegrees(-90));poses.mulPose(Axis.ZP.rotationDegrees(-90));}
            case WEST->{poses.mulPose(Axis.YP.rotationDegrees(180));poses.mulPose(Axis.ZP.rotationDegrees(-90));}
            case EAST->poses.mulPose(Axis.ZP.rotationDegrees(-90));
        }
        poses.translate(-.05,.315F-.5F,.15);poses.scale(.0125F,-.0125F,.0125F);poses.mulPose(Axis.YP.rotationDegrees(90));poses.mulPose(Axis.XP.rotationDegrees(90));
        LegacyFonts.get().drawInBatch(charge.display(),0,0,0x00FF00,false,poses.last().pose(),buffers,Font.DisplayMode.NORMAL,0,light);
        poses.popPose();
    }
}
