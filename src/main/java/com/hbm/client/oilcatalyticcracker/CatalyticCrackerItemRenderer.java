package com.hbm.client.oilcatalyticcracker;
import com.hbm.client.model.LegacyItemTransforms;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;
/** Original global ItemRenderLibrary888: inventory y−3.5/scale1.8, common scale.5, no extra yaw. */
public final class CatalyticCrackerItemRenderer extends BlockEntityWithoutLevelRenderer {
 public CatalyticCrackerItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
 @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack pose,MultiBufferSource buffers,int light,int overlay){pose.pushPose();if(LegacyItemTransforms.isHeld(context))LegacyItemTransforms.held(pose,context);else pose.translate(.5,.5,.5);if(context==ItemDisplayContext.GUI){pose.scale(1F/16,-1F/16,1F/16);pose.translate(0,2,0);pose.mulPose(Axis.XP.rotationDegrees(-30));pose.mulPose(Axis.YP.rotationDegrees(45));pose.scale(-1,-1,-1);pose.translate(0,-3.5,0);pose.scale(1.8F,1.8F,1.8F);}else{boolean entity=context==ItemDisplayContext.GROUND||context==ItemDisplayContext.FIXED||context==ItemDisplayContext.NONE;if(entity)pose.scale(1.5F,1.5F,1.5F);else pose.translate(.5,.25,0);pose.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)pose.mulPose(Axis.YP.rotationDegrees(90));}pose.scale(.5F,.5F,.5F);CatalyticCrackerRenderer.draw(false,pose,buffers,light,overlay);pose.popPose();}
}
