package com.hbm.client.barrel;

import com.hbm.block.pipe.NativePipeBlock;
import com.hbm.client.model.*;
import com.hbm.fluid.HbmFluids;
import com.hbm.machine.barrel.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

/** Source RenderFluidBarrel TESR, in addition to the static RenderBarrel body. */
public final class BarrelWorldRenderer implements BlockEntityRenderer<BarrelBlockEntity> {
    public BarrelWorldRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(BarrelBlockEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        var type=machine.tank.getTankType();if(type==HbmFluids.NONE||machine.getLevel()==null)return;var variant=((BarrelBlock)machine.getBlockState().getBlock()).variant;var texture=ResourceLocation.parse("hbm:textures/block/"+variant.id()+".png");var model=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/blocks/barrel.obj"));int mask=NativePipeBlock.mask(machine.getLevel(),machine.getBlockPos(),type);
        poses.pushPose();poses.translate(.5,.5,.5);
        for(var side:new Direction[]{Direction.EAST,Direction.WEST,Direction.NORTH,Direction.SOUTH})if((mask&NativePipeBlock.bit(side))!=0){poses.pushPose();poses.mulPose(Axis.YP.rotationDegrees(switch(side){case WEST->180;case NORTH->90;case SOUTH->-90;default->0;}));poses.translate(0,-.5,0);LegacyObjRenderer.renderOnly(model,poses,buffers.getBuffer(RenderType.entityCutout(texture)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR_FLAT,"Connector");poses.popPose();}
        for(int i=0;i<4;i++){poses.pushPose();poses.translate(.4,.30,-.24);poses.scale(1,.25F,.25F);LegacyDangerDiamond.render(poses,buffers,light,type);poses.popPose();poses.mulPose(Axis.YP.rotationDegrees(90));}
        poses.popPose();
    }
}
