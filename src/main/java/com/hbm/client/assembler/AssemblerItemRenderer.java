package com.hbm.client.assembler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;

/** Original ItemRenderBase transform and RenderAssemblyMachine's -2.75Y,4.5 GUI,90Y,.75 common scale. */
public final class AssemblerItemRenderer extends BlockEntityWithoutLevelRenderer {
    public AssemblerItemRenderer() { super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels()); }
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay) {
        poses.pushPose();
        com.hbm.client.model.LegacyItemTransforms.held(poses,context);
        if(context==ItemDisplayContext.GUI) {
            poses.translate(0,1,.5); poses.scale(1F/16,-1F/16,1F/16);
            poses.translate(8,10,0); poses.mulPose(Axis.XP.rotationDegrees(-30)); poses.mulPose(Axis.YP.rotationDegrees(45)); poses.scale(-1,-1,-1);
            poses.translate(0,-2.75,0); poses.scale(4.5F,4.5F,4.5F);
        } else {
            if(context==ItemDisplayContext.GROUND) { poses.translate(.5,0,.5); poses.scale(1.5F,1.5F,1.5F); }
            else poses.translate(.5,.25,0);
            poses.scale(.25F,.25F,.25F);
            if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) poses.mulPose(Axis.YP.rotationDegrees(90));
        }
        poses.mulPose(Axis.YP.rotationDegrees(90));
        poses.scale(.75F,.75F,.75F);
        AssemblerRenderer.all(poses,buffers,light,overlay); poses.popPose();
    }
}
