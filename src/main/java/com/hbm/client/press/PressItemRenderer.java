package com.hbm.client.press;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** ItemRenderLibrary's press body/head and ItemRenderBase display-specific transforms. */
public final class PressItemRenderer extends BlockEntityWithoutLevelRenderer {
    public PressItemRenderer() { super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels()); }
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay) {
        poses.pushPose();
        com.hbm.client.model.LegacyItemTransforms.held(poses,context);
        if(context==ItemDisplayContext.GUI) {
            // Pixel (8,10), GUI -30X/+45Y, negative axes, offset -4 and scale4.5 in a16px square.
            poses.translate(.5,.375,.5); poses.mulPose(Axis.XP.rotationDegrees(30)); poses.mulPose(Axis.YP.rotationDegrees(45));
            poses.translate(0,-.25,0); poses.scale(-.28125F,.28125F,-.28125F);
        } else {
            boolean entity=context==ItemDisplayContext.GROUND;
            if(!entity) poses.translate(.5,.25,0); else { poses.translate(.5,0,.5); poses.scale(1.5F,1.5F,1.5F); }
            poses.scale(.25F,.25F,.25F);
            if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND && context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) poses.mulPose(Axis.YP.rotationDegrees(90));
        }
        PressRenderer.mesh(PressRenderer.BODY,PressRenderer.BODY_TEXTURE,poses,buffers,light,overlay);
        poses.translate(0,.5,0); PressRenderer.mesh(PressRenderer.HEAD,PressRenderer.HEAD_TEXTURE,poses,buffers,light,overlay);
        poses.popPose();
    }
}
