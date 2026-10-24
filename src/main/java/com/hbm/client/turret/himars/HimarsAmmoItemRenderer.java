package com.hbm.client.turret.himars;
import com.hbm.client.model.LegacyItemTransforms;
import com.hbm.machine.turret.himars.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;
/** Exact ItemRenderLibrary1038: GUI(0,-2,0)*2.75 plus source time spin, real rocket and its tube, unchanged ItemRenderBase outer display matrices, no rotor or extra beam. */
public final class HimarsAmmoItemRenderer extends BlockEntityWithoutLevelRenderer {
 public HimarsAmmoItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
 @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
  pose.pushPose();try{
   LegacyItemTransforms.held(pose,context);
   if(context==ItemDisplayContext.GUI){pose.translate(0,1,.5);pose.scale(1F/16,-1F/16,1F/16);pose.translate(8,10,0);pose.mulPose(Axis.XP.rotationDegrees(-30));pose.mulPose(Axis.YP.rotationDegrees(45));pose.scale(-1,-1,-1);pose.translate(0,-2,0);pose.scale(2.75F,2.75F,2.75F);pose.mulPose(Axis.YP.rotationDegrees((float)(System.currentTimeMillis()%3600/10D)));}
   else{if(context==ItemDisplayContext.GROUND){pose.translate(.5,0,.5);pose.scale(1.5F,1.5F,1.5F);}else pose.translate(.5,.25,0);pose.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)pose.mulPose(Axis.YP.rotationDegrees(90));}
   pose.translate(0,1.5,0);pose.mulPose(Axis.YP.rotationDegrees(-45));pose.mulPose(Axis.XP.rotationDegrees(90));var type=HimarsRocketTypes.type(HimarsAmmoItem.type(stack));pose.translate(.75,0,0);HimarsTurretRenderer.part(HimarsTurretRenderer.MODEL,type.texture(),pose,buffers,light,overlay,type.modelType()==0?"RocketStandard":"RocketSingle");pose.translate(-1.5,0,0);pose.translate(0,-3.375,0);HimarsTurretRenderer.part(HimarsTurretRenderer.MODEL,type.texture(),pose,buffers,light,overlay,type.modelType()==0?"TubeStandard":"TubeSingle");
  }finally{pose.popPose();}
 }
}
