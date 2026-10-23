package com.hbm.client.cargoelevator;

import com.hbm.client.model.LegacyItemTransforms;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;

/** Source automatic IItemRendererProvider, including its distinct three-level inventory preview. */
public final class CargoElevatorItemRenderer extends BlockEntityWithoutLevelRenderer {
    public CargoElevatorItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        poses.pushPose();if(LegacyItemTransforms.isHeld(context))LegacyItemTransforms.held(poses,context);else poses.translate(.5,.5,.5);
        if(context==ItemDisplayContext.GUI){poses.scale(1F/16,-1F/16,1F/16);poses.translate(0,2,0);poses.mulPose(Axis.XP.rotationDegrees(-30));poses.mulPose(Axis.YP.rotationDegrees(45));poses.scale(-1,-1,-1);poses.translate(0,-2.75,0);poses.scale(3.25F,3.25F,3.25F);}
        else{boolean entity=context==ItemDisplayContext.GROUND||context==ItemDisplayContext.FIXED||context==ItemDisplayContext.NONE;if(entity)poses.scale(1.5F,1.5F,1.5F);else poses.translate(.5,.25,0);poses.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)poses.mulPose(Axis.YP.rotationDegrees(90));}
        CargoElevatorRenderer.part(poses,buffers,light,overlay,"Base");CargoElevatorRenderer.part(poses,buffers,light,overlay,"Piston");CargoElevatorRenderer.part(poses,buffers,light,overlay,"Guides");poses.translate(0,1,0);CargoElevatorRenderer.part(poses,buffers,light,overlay,"Piston");CargoElevatorRenderer.part(poses,buffers,light,overlay,"Guides");CargoElevatorRenderer.part(poses,buffers,light,overlay,"Platform");poses.translate(0,1,0);CargoElevatorRenderer.part(poses,buffers,light,overlay,"Guides");poses.popPose();
    }
}
