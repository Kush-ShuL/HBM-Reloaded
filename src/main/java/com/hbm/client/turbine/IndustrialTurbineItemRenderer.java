package com.hbm.client.turbine;
import com.hbm.client.model.LegacyItemTransforms;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;
/** Original item animation intentionally retains the gauge135-degree transform on its flywheel. */
public final class IndustrialTurbineItemRenderer extends BlockEntityWithoutLevelRenderer {
    public IndustrialTurbineItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        poses.pushPose();if(LegacyItemTransforms.isHeld(context))LegacyItemTransforms.held(poses,context);else poses.translate(.5,.5,.5);
        if(context==ItemDisplayContext.GUI){poses.scale(1F/16,-1F/16,1F/16);poses.translate(0,2,0);poses.mulPose(Axis.XP.rotationDegrees(-30));poses.mulPose(Axis.YP.rotationDegrees(45));poses.scale(-1,-1,-1);poses.translate(1,0,0);poses.scale(3,3,3);}
        else{boolean entity=context==ItemDisplayContext.GROUND||context==ItemDisplayContext.FIXED||context==ItemDisplayContext.NONE;if(entity)poses.scale(1.5F,1.5F,1.5F);else poses.translate(.5,.25,0);poses.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)poses.mulPose(Axis.YP.rotationDegrees(90));}
        poses.mulPose(Axis.YP.rotationDegrees(90));poses.scale(.75F,.75F,.75F);poses.translate(.5,0,0);IndustrialTurbineRenderer.part(poses,buffers,light,overlay,"Turbine");
        poses.translate(0,1.5,0);poses.mulPose(Axis.ZP.rotationDegrees(135));poses.translate(0,-1.5,0);IndustrialTurbineRenderer.part(poses,buffers,light,overlay,"Gauge");
        double rotation=(System.currentTimeMillis()/5)%336D;poses.translate(0,1.5,0);poses.mulPose(Axis.ZN.rotationDegrees((float)rotation));poses.translate(0,-1.5,0);IndustrialTurbineRenderer.part(poses,buffers,light,overlay,"Flywheel");poses.popPose();
    }
}
