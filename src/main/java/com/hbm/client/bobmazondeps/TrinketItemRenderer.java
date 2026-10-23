package com.hbm.client.bobmazondeps;

import com.hbm.client.model.LegacyItemTransforms;
import com.hbm.item.bobmazondeps.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;

/** Source ItemRenderBase caller matrices and each original provider's distinct inventory/common transforms. */
public final class TrinketItemRenderer extends BlockEntityWithoutLevelRenderer {
 private final boolean snowglobe;
 public TrinketItemRenderer(boolean snowglobe){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());this.snowglobe=snowglobe;}
 @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
  poses.pushPose();if(LegacyItemTransforms.isHeld(context))LegacyItemTransforms.held(poses,context);else poses.translate(.5,.5,.5);
  if(context==ItemDisplayContext.GUI){poses.scale(1F/16,-1F/16,1F/16);poses.translate(0,2,0);poses.mulPose(Axis.XP.rotationDegrees(-30));poses.mulPose(Axis.YP.rotationDegrees(45));poses.scale(-1,-1,-1);poses.translate(0,snowglobe?-2:-6,0);poses.scale(6,6,6);}
  else{boolean entity=context==ItemDisplayContext.GROUND||context==ItemDisplayContext.FIXED||context==ItemDisplayContext.NONE;if(entity)poses.scale(1.5F,1.5F,1.5F);else poses.translate(.5,.25,0);poses.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)poses.mulPose(Axis.YP.rotationDegrees(90));}
  poses.translate(0,.25,0);int meta=BobmazonDependenciesContent.metadata(stack);
  if(snowglobe){poses.scale(3,3,3);SnowglobeRenderer.draw(TrinketTypes.Snowglobe.metadata(meta),poses,buffers,light,overlay);}else{var type=TrinketTypes.Plushie.metadata(meta);switch(type){case NONE->{}case YOMI->poses.scale(1.25F,1.25F,1.25F);case NUMBERNINE->{poses.translate(0,.25,.25);poses.scale(1.25F,1.25F,1.25F);}case HUNDUN->{poses.translate(.5,.5,0);poses.scale(1.25F,1.25F,1.25F);}case DERG->poses.scale(1.5F,1.5F,1.5F);}PlushieRenderer.draw(type,false,poses,buffers,light,overlay);}
  poses.popPose();
 }
}
