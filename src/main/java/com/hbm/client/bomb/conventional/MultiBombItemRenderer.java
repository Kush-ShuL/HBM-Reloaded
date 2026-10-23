package com.hbm.client.bomb.conventional;

import com.hbm.client.model.LegacyItemTransforms;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** Original ItemRenderLibrary's special inventory/common matrices for the assembled generic bomb. */
public final class MultiBombItemRenderer extends BlockEntityWithoutLevelRenderer {
    public MultiBombItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){poses.pushPose();LegacyItemTransforms.held(poses,context);
        if(context==ItemDisplayContext.GUI){poses.translate(0,1,.5);poses.scale(1/16F,-1/16F,1/16F);poses.translate(8,10,0);poses.mulPose(Axis.XP.rotationDegrees(-30));poses.mulPose(Axis.YP.rotationDegrees(45));poses.scale(-1,-1,-1);poses.translate(0,-1,0);poses.scale(4,4,4);}
        else{if(context==ItemDisplayContext.GROUND){poses.translate(.5,0,.5);poses.scale(1.5F,1.5F,1.5F);}else poses.translate(.5,.25,0);poses.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)poses.mulPose(Axis.YP.rotationDegrees(90));}
        poses.translate(.75,0,0);poses.scale(3,3,3);poses.translate(0,.5,0);poses.mulPose(Axis.XP.rotationDegrees(180));poses.mulPose(Axis.YP.rotationDegrees(90));MultiBombRenderer.mesh(poses,buffers,light,overlay);poses.popPose();}
}
