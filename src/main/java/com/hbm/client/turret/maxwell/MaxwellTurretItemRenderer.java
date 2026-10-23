package com.hbm.client.turret.maxwell;
import com.hbm.client.model.LegacyItemTransforms;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;
/** Exact ItemRenderLibrary735: GUI(-1,-3,0)*4, unchanged ItemRenderBase outer display matrices, no rotor or extra beam. */
public final class MaxwellTurretItemRenderer extends BlockEntityWithoutLevelRenderer {
 public MaxwellTurretItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
 @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
  pose.pushPose();try{
   LegacyItemTransforms.held(pose,context);
   if(context==ItemDisplayContext.GUI){pose.translate(0,1,.5);pose.scale(1F/16,-1F/16,1F/16);pose.translate(8,10,0);pose.mulPose(Axis.XP.rotationDegrees(-30));pose.mulPose(Axis.YP.rotationDegrees(45));pose.scale(-1,-1,-1);pose.translate(-1,-3,0);pose.scale(4,4,4);}
   else{if(context==ItemDisplayContext.GROUND){pose.translate(.5,0,.5);pose.scale(1.5F,1.5F,1.5F);}else pose.translate(.5,.25,0);pose.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)pose.mulPose(Axis.YP.rotationDegrees(90));}
   MaxwellTurretRenderer.body(pose,buffers,light,overlay);
  }finally{pose.popPose();}
 }
}
