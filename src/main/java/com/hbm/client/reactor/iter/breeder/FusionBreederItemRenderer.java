package com.hbm.client.reactor.iter.breeder;
import com.hbm.client.model.LegacyItemTransforms;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;
/** Original ItemRenderBase plus authored inventoryT(0,-3,0)S5Y90/commonS.5Y90. */
public final class FusionBreederItemRenderer extends BlockEntityWithoutLevelRenderer {
 public FusionBreederItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
 @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack pose,MultiBufferSource buffers,int light,int overlay){pose.pushPose();LegacyItemTransforms.held(pose,context);if(context==ItemDisplayContext.GUI){pose.translate(0,1,.5);pose.scale(1F/16,-1F/16,1F/16);pose.translate(8,10,0);pose.mulPose(Axis.XP.rotationDegrees(-30));pose.mulPose(Axis.YP.rotationDegrees(45));pose.scale(-1,-1,-1);pose.translate(0,-3,0);pose.scale(5,5,5);pose.mulPose(Axis.YP.rotationDegrees(90));}else{if(context==ItemDisplayContext.GROUND){pose.translate(.5,0,.5);pose.scale(1.5F,1.5F,1.5F);}else pose.translate(.5,.25,0);pose.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)pose.mulPose(Axis.YP.rotationDegrees(90));}pose.scale(.5F,.5F,.5F);pose.mulPose(Axis.YP.rotationDegrees(90));FusionBreederRenderer.model(pose,buffers,light,overlay);pose.popPose();}
}
