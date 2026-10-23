package com.hbm.client.bomb.conventional;

import com.hbm.bomb.conventional.LandmineBlock;
import com.hbm.client.model.LegacyItemTransforms;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** Original ItemRenderLibrary AP/HE ItemRenderBase overrides. */
public final class LandmineItemRenderer extends BlockEntityWithoutLevelRenderer {
    public LandmineItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        var kind=((LandmineBlock)((BlockItem)stack.getItem()).getBlock()).kind();poses.pushPose();LegacyItemTransforms.held(poses,context);
        if(context==ItemDisplayContext.GUI){poses.translate(0,1,.5);poses.scale(1/16F,-1/16F,1/16F);poses.translate(8,10,0);poses.mulPose(Axis.XP.rotationDegrees(-30));poses.mulPose(Axis.YP.rotationDegrees(45));poses.scale(-1,-1,-1);if(kind==LandmineBlock.Kind.NAVAL)poses.translate(0,2,-1);if(kind==LandmineBlock.Kind.FAT)poses.translate(0,-1,0);float scale=kind==LandmineBlock.Kind.NAVAL?5:kind==LandmineBlock.Kind.HE?6:kind==LandmineBlock.Kind.FAT?7:8;poses.scale(scale,scale,scale);}
        else{boolean entity=context==ItemDisplayContext.GROUND;if(entity){poses.translate(.5,0,.5);poses.scale(1.5F,1.5F,1.5F);}else poses.translate(.5,.25,0);poses.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)poses.mulPose(Axis.YP.rotationDegrees(90));if(kind==LandmineBlock.Kind.HE){poses.translate(.25,.625,0);poses.mulPose(Axis.YP.rotationDegrees(45));poses.mulPose(Axis.ZP.rotationDegrees(-15));}}
        float scale=kind==LandmineBlock.Kind.NAVAL||kind==LandmineBlock.Kind.FAT?1:kind==LandmineBlock.Kind.HE?4:1.25F;poses.scale(scale,scale,scale);if(kind==LandmineBlock.Kind.FAT){poses.translate(.25,0,0);poses.mulPose(Axis.YP.rotationDegrees(90));}LandmineRenderer.mesh(kind,switch(kind){case AP->"mine_ap_grass";case HE->"mine_marelet";case SHRAP->"mine_shrapnel";case NAVAL->"NMine";case FAT->"mine_fat";},poses,buffers,light,overlay);poses.popPose();
    }
}
