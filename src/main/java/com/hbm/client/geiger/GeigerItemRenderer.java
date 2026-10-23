package com.hbm.client.geiger;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** RenderGeiger's ItemRenderBase GUI scale10 and common translate(.2,0,0)/rotateY90. */
public final class GeigerItemRenderer extends BlockEntityWithoutLevelRenderer {
    public GeigerItemRenderer() { super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels()); }
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay) {
        poses.pushPose();
        com.hbm.client.model.LegacyItemTransforms.held(poses,context);
        if(context==ItemDisplayContext.GUI) {
            poses.translate(.5,.375,.5);poses.mulPose(Axis.XP.rotationDegrees(30));poses.mulPose(Axis.YP.rotationDegrees(45));poses.scale(-.625F,.625F,-.625F);
        } else {
            if(context==ItemDisplayContext.GROUND) { poses.translate(.5,0,.5);poses.scale(1.5F,1.5F,1.5F); }
            else poses.translate(.5,.25,0);
            poses.scale(.25F,.25F,.25F);
            if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) poses.mulPose(Axis.YP.rotationDegrees(90));
        }
        poses.translate(.2,0,0);poses.mulPose(Axis.YP.rotationDegrees(90));GeigerRenderer.mesh(poses,buffers,light,overlay);poses.popPose();
    }
}
