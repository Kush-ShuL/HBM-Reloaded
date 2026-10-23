package com.hbm.block.pipe.client;

import com.hbm.block.pipe.PipeBlockItem;
import com.hbm.block.pipe.NativePipeBlock;
import com.hbm.block.pipe.PipeKind;
import com.hbm.hazard.HbmHazards;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** RenderBoxDuct inventory bounds and RenderPipeAnchor/ItemRenderBase original transforms. */
public final class SpecialPipeItemRenderer extends BlockEntityWithoutLevelRenderer {
    public SpecialPipeItemRenderer() { super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels()); }
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay) {
        if (!(stack.getItem() instanceof PipeBlockItem item)) return;
        PipeKind kind=((NativePipeBlock)item.getBlock()).kind();
        poses.pushPose();
        if (kind.box()) {
            SpecialPipeRenderer.box(poses,buffers,light,overlay,kind,Math.abs(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)%15),3,0xFFFFFF);
        } else if (kind.anchor()) {
            if(com.hbm.client.model.LegacyItemTransforms.isHeld(context)) com.hbm.client.model.LegacyItemTransforms.held(poses,context);
            else poses.translate(.5,.5,.5);
            if (context==ItemDisplayContext.GUI) {
                poses.scale(1F/16F,-1F/16F,1F/16F); poses.translate(0,2,0);
                poses.mulPose(Axis.XP.rotationDegrees(-30)); poses.mulPose(Axis.YP.rotationDegrees(45)); poses.scale(-1,-1,-1);
                poses.translate(0,-3.5,0); poses.scale(10,10,10);
            } else {
                boolean entity=context==ItemDisplayContext.GROUND||context==ItemDisplayContext.FIXED||context==ItemDisplayContext.NONE;
                boolean equipped=context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND||context==ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                if (entity) poses.scale(1.5F,1.5F,1.5F); else poses.translate(.5,.25,0);
                poses.scale(.25F,.25F,.25F); if(!equipped) poses.mulPose(Axis.YP.rotationDegrees(90));
            }
            SpecialPipeRenderer.anchorPart(poses,buffers,light,overlay,kind,0xFFFFFF,"Anchor");
        }
        poses.popPose();
    }
}
