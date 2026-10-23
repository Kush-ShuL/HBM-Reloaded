package com.hbm.world.structure.templates.heatboiler.client;
import com.hbm.client.model.LegacyItemTransforms;
import com.hbm.world.structure.templates.heatboiler.HeatBoilerContent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;
/** Actual ItemRenderBase plus RenderBoiler's GUI T(0,-3,0)*S(3) and commonY90. */
public final class HeatBoilerItemRenderer extends BlockEntityWithoutLevelRenderer {
    public HeatBoilerItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){poses.pushPose();LegacyItemTransforms.held(poses,context);if(context==ItemDisplayContext.GUI){poses.translate(0,1,.5);poses.scale(1F/16,-1F/16,1F/16);poses.translate(8,10,0);poses.mulPose(Axis.XP.rotationDegrees(-30));poses.mulPose(Axis.YP.rotationDegrees(45));poses.scale(-1,-1,-1);poses.translate(0,-3,0);poses.scale(3,3,3);}else{if(context==ItemDisplayContext.GROUND){poses.translate(.5,0,.5);poses.scale(1.5F,1.5F,1.5F);}else poses.translate(.5,.25,0);poses.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)poses.mulPose(Axis.YP.rotationDegrees(90));}poses.mulPose(Axis.YP.rotationDegrees(90));HeatBoilerRenderer.model(HeatBoilerContent.metadata(stack)==1,true,poses,buffers,light,overlay);poses.popPose();}
}
