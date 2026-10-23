package com.hbm.client.pylon;

import com.hbm.block.pylon.PylonBlockItem;
import com.hbm.block.pylon.PylonKind;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** Original ItemRenderBase plus each source renderer's inventory/common transforms. */
public final class PylonItemRenderer extends BlockEntityWithoutLevelRenderer {
    public PylonItemRenderer() { super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels()); }
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay) {
        if(!(stack.getItem() instanceof PylonBlockItem item)) return;PylonKind kind=item.kind;
        poses.pushPose();
        if(com.hbm.client.model.LegacyItemTransforms.isHeld(context)) com.hbm.client.model.LegacyItemTransforms.held(poses,context);
        else poses.translate(.5,.5,.5);
        if(context==ItemDisplayContext.GUI) {
            poses.scale(1F/16,-1F/16,1F/16);poses.translate(0,2,0);poses.mulPose(Axis.XP.rotationDegrees(-30));poses.mulPose(Axis.YP.rotationDegrees(45));poses.scale(-1,-1,-1);
            double x=kind.medium()?1:0,y=kind==PylonKind.CONNECTOR?-3.5:kind==PylonKind.SUBSTATION?-2.5:-5;
            float scale=kind.connector()?7:kind.medium()||kind==PylonKind.SUBSTATION?4.5F:kind==PylonKind.LARGE?2.25F:2.9F;
            poses.translate(x,y,0);poses.scale(scale,scale,scale);
        } else {
            boolean entity=context==ItemDisplayContext.GROUND||context==ItemDisplayContext.FIXED||context==ItemDisplayContext.NONE;
            if(entity) poses.scale(1.5F,1.5F,1.5F);else poses.translate(.5,.25,0);
            poses.scale(.25F,.25F,.25F);
            if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) poses.mulPose(Axis.YP.rotationDegrees(90));
        }
        if(kind.medium()) { poses.mulPose(Axis.YP.rotationDegrees(90));poses.scale(.5F,.5F,.5F);poses.translate(.75,0,0); }
        else if(kind==PylonKind.LARGE||kind==PylonKind.SUBSTATION) poses.scale(.5F,.5F,.5F);
        else if(kind.connector()) poses.scale(2,2,2);
        PylonModels.draw(kind,poses,buffers,light,overlay,true);poses.popPose();
    }
}
