package com.hbm.client.electricpress;

import com.hbm.client.model.LegacyItemTransforms;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;

/** Actual ItemRenderLibrary70 and RenderConveyorPress ItemRenderBase; both GUI-4/scale4.5,with their distinct common parts. */
public final class ElectricPressItemRenderer extends BlockEntityWithoutLevelRenderer {
    private final boolean conveyor;
    public ElectricPressItemRenderer(boolean conveyor){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());this.conveyor=conveyor;}
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack pose,MultiBufferSource buffers,int light,int overlay){pose.pushPose();LegacyItemTransforms.held(pose,context);if(context==ItemDisplayContext.GUI){pose.translate(0,1,.5);pose.scale(1F/16,-1F/16,1F/16);pose.translate(8,10,0);pose.mulPose(Axis.XP.rotationDegrees(-30));pose.mulPose(Axis.YP.rotationDegrees(45));pose.scale(-1,-1,-1);pose.translate(0,-4,0);pose.scale(4.5F,4.5F,4.5F);}else{if(context==ItemDisplayContext.GROUND){pose.translate(.5,0,.5);pose.scale(1.5F,1.5F,1.5F);}else pose.translate(.5,.25,0);pose.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)pose.mulPose(Axis.YP.rotationDegrees(90));}
        if(conveyor){ConveyorPressRenderer.parts(pose,buffers,light,overlay,"Press","Piston");ConveyorPressRenderer.belt(pose,buffers,light,overlay,0);}else{ElectricPressRenderer.draw(ElectricPressRenderer.BODY,ElectricPressRenderer.BODY_TEX,pose,buffers,light,overlay);pose.translate(0,1.5,0);ElectricPressRenderer.draw(ElectricPressRenderer.HEAD,ElectricPressRenderer.HEAD_TEX,pose,buffers,light,overlay);}pose.popPose();}
}
