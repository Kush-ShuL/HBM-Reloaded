package com.hbm.client.fluidtank;

import com.hbm.client.model.LegacyItemTransforms;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;

/** Exact original ItemRenderBase transforms and RenderChemicalPlant's common transform. */
public final class IndustrialFluidTankItemRenderer extends BlockEntityWithoutLevelRenderer {
    public IndustrialFluidTankItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        poses.pushPose();LegacyItemTransforms.held(poses,context);
        if(context==ItemDisplayContext.GUI){
            poses.translate(0,1,.5);poses.scale(1F/16,-1F/16,1F/16);poses.translate(8,10,0);
            poses.mulPose(Axis.XP.rotationDegrees(-30));poses.mulPose(Axis.YP.rotationDegrees(45));poses.scale(-1,-1,-1);
            poses.translate(0,-2,0);poses.scale(3.5F,3.5F,3.5F);
        }else{
            if(context==ItemDisplayContext.GROUND){poses.translate(.5,0,.5);poses.scale(1.5F,1.5F,1.5F);}else poses.translate(.5,.25,0);
            poses.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)poses.mulPose(Axis.YP.rotationDegrees(90));
        }
        poses.mulPose(Axis.YP.rotationDegrees(90));poses.scale(.75F,.75F,.75F);var tank=new com.hbm.fluid.FluidTank(com.hbm.fluid.HbmFluids.NONE,0);boolean exploded=false;var custom=stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);if(custom!=null&&custom.copyTag().contains("persistent")){var data=custom.copyTag().getCompound("persistent");tank.readFromNBT(data,"tank");exploded=data.getBoolean("hasExploded");}IndustrialFluidTankRenderer.model(poses,buffers,light,overlay,tank.getTankType(),exploded);poses.popPose();
    }
}
