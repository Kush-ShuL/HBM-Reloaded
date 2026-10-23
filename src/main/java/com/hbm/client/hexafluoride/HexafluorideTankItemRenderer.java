package com.hbm.client.hexafluoride;

import com.hbm.client.model.LegacyItemTransforms;
import com.hbm.machine.hexafluoride.HexafluorideTankContent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;

/** True ItemRenderLibrary474/484: source GUI T(0,-4,0)*S6; common Y(-90), ordinary ItemRenderBase caller. */
public final class HexafluorideTankItemRenderer extends BlockEntityWithoutLevelRenderer {
    public HexafluorideTankItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        poses.pushPose();
        if(LegacyItemTransforms.isHeld(context))LegacyItemTransforms.held(poses,context);else poses.translate(.5,.5,.5);
        if(context==ItemDisplayContext.GUI){
            poses.scale(1F/16,-1F/16,1F/16);poses.translate(0,2,0);
            poses.mulPose(Axis.XP.rotationDegrees(-30));poses.mulPose(Axis.YP.rotationDegrees(45));poses.scale(-1,-1,-1);
            poses.translate(0,-4,0);poses.scale(6,6,6);
        }else{
            boolean entity=context==ItemDisplayContext.GROUND||context==ItemDisplayContext.FIXED||context==ItemDisplayContext.NONE;
            if(entity)poses.scale(1.5F,1.5F,1.5F);else poses.translate(.5,.25,0);
            poses.scale(.25F,.25F,.25F);
            if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)poses.mulPose(Axis.YP.rotationDegrees(90));
        }
        poses.mulPose(Axis.YP.rotationDegrees(-90));
        HexafluorideTankRenderer.all(stack.is(HexafluorideTankContent.PUF6_ITEM.get()),poses,buffers,light,overlay);poses.popPose();
    }
}
