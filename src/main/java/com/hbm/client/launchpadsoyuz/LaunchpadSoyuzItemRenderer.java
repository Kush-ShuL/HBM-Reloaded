package com.hbm.client.launchpadsoyuz;

import com.hbm.client.model.LegacyItemTransforms;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;

/** Actual RenderLaunchpadSoyuz IItemRendererProvider: GUI T(6,-1.5,0)S1.125Ry90, common S.25 and five untranslated item struts. */
public final class LaunchpadSoyuzItemRenderer extends BlockEntityWithoutLevelRenderer {
    public LaunchpadSoyuzItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        pose.pushPose();try{
            if(LegacyItemTransforms.isHeld(context))LegacyItemTransforms.held(pose,context);else pose.translate(.5,.5,.5);
            if(context==ItemDisplayContext.GUI){pose.scale(1F/16,-1F/16,1F/16);pose.translate(0,2,0);pose.mulPose(Axis.XP.rotationDegrees(-30));pose.mulPose(Axis.YP.rotationDegrees(45));pose.scale(-1,-1,-1);pose.translate(6,-1.5,0);pose.scale(1.125F,1.125F,1.125F);pose.mulPose(Axis.YP.rotationDegrees(90));}
            else{if(context==ItemDisplayContext.GROUND||context==ItemDisplayContext.FIXED||context==ItemDisplayContext.NONE)pose.scale(1.5F,1.5F,1.5F);else pose.translate(.5,.25,0);pose.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)pose.mulPose(Axis.YP.rotationDegrees(90));}
            LaunchpadSoyuzModels.inventory(pose,buffers,light,overlay);
        }finally{pose.popPose();}
    }
}
