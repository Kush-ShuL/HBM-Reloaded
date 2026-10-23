package com.hbm.client.refueler;

import com.hbm.machine.refueler.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;

public final class RefuelerRenderer implements BlockEntityRenderer<RefuelerEntity> {
    public RefuelerRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(RefuelerEntity station,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        poses.pushPose();try{
            poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(90));
            float rotation=switch(station.getBlockState().getValue(RefuelerBlock.META)){case 4->90;case 3->180;case 5->270;default->0;};poses.mulPose(Axis.YP.rotationDegrees(rotation));
            RefuelerModels.body(poses,buffers,light,overlay);
            RefuelerModels.fluid(poses,buffers,light,overlay,station.tank.getTankType().getColor(),station.prevFillLevel+(station.fillLevel-station.prevFillLevel)*partial);
        }finally{poses.popPose();}
    }
}
