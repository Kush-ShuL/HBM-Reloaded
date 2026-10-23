package com.hbm.client.mininglaser;
import com.hbm.client.model.LegacyItemTransforms;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;
/** Source explicit ItemRenderLibrary213: GUI T(0,-.5,0)/S3; common Base/Pivot and Laser T(0,-1,.75)/Rx90. */
public final class MiningLaserItemRenderer extends BlockEntityWithoutLevelRenderer {
    public MiningLaserItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        pose.pushPose();LegacyItemTransforms.held(pose,context);
        if(context==ItemDisplayContext.GUI){pose.translate(0,1,.5);pose.scale(1F/16,-1F/16,1F/16);pose.translate(8,10,0);pose.mulPose(Axis.XP.rotationDegrees(-30));pose.mulPose(Axis.YP.rotationDegrees(45));pose.scale(-1,-1,-1);pose.translate(0,-.5,0);pose.scale(3,3,3);}
        else{if(context==ItemDisplayContext.GROUND){pose.translate(.5,0,.5);pose.scale(1.5F,1.5F,1.5F);}else pose.translate(.5,.25,0);pose.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)pose.mulPose(Axis.YP.rotationDegrees(90));}
        MiningLaserRenderer.part("Base","base",pose,buffers,light,overlay);MiningLaserRenderer.part("Pivot","pivot",pose,buffers,light,overlay);pose.translate(0,-1,.75);pose.mulPose(Axis.XP.rotationDegrees(90));MiningLaserRenderer.part("Laser","laser",pose,buffers,light,overlay);pose.popPose();
    }
}
