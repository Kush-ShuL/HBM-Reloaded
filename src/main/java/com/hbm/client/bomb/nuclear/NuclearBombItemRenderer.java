package com.hbm.client.bomb.nuclear;

import com.hbm.bomb.nuclear.NuclearBombBlock;
import com.hbm.client.model.LegacyItemTransforms;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** ItemRenderBase and each original nuclear item's inventory/common matrices. */
public final class NuclearBombItemRenderer extends BlockEntityWithoutLevelRenderer {
 public NuclearBombItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
 @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
  String kind=((NuclearBombBlock)((BlockItem)stack.getItem()).getBlock()).kind();poses.pushPose();LegacyItemTransforms.held(poses,context);
  if(context==ItemDisplayContext.GUI){poses.translate(0,1,.5);poses.scale(1F/16,-1F/16,1F/16);poses.translate(8,10,0);poses.mulPose(Axis.XP.rotationDegrees(-30));poses.mulPose(Axis.YP.rotationDegrees(45));poses.scale(-1,-1,-1);switch(kind){case "gadget"->{poses.translate(0,-3,0);poses.scale(5,5,5);}case "boy"->poses.scale(5,5,5);case "man"->{poses.translate(0,-2,0);poses.scale(5,5,5);}case "mike","n2"->{poses.translate(0,-5,0);poses.scale(2.25F,2.25F,2.25F);}case "tsar"->poses.scale(2.25F,2.25F,2.25F);case "fleija"->poses.scale(6.8F,6.8F,6.8F);case "solinium"->{poses.translate(0,-.125,0);poses.scale(5,5,5);}case "prototype"->{poses.translate(0,.125,0);poses.scale(3,3,3);}}}
  else{if(context==ItemDisplayContext.GROUND){poses.translate(.5,0,.5);poses.scale(1.5F,1.5F,1.5F);}else poses.translate(.5,.25,0);poses.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)poses.mulPose(Axis.YP.rotationDegrees(90));}
  switch(kind){case "gadget"->poses.mulPose(Axis.YP.rotationDegrees(-90));case "boy"->poses.translate(-1,0,0);case "man"->{poses.mulPose(Axis.YP.rotationDegrees(180));poses.translate(-.75,0,0);}case "tsar"->poses.translate(1.5,0,0);case "fleija"->{poses.translate(.125,0,0);poses.mulPose(Axis.YP.rotationDegrees(90));}case "solinium","prototype"->{poses.mulPose(Axis.YP.rotationDegrees(90));poses.translate(0,kind.equals("solinium")?-.125:.125,0);}}
  NuclearBombRenderer.draw(kind,poses,buffers,light,overlay,true);poses.popPose();
 }
}
