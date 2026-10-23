package com.hbm.client.electricheater;
import com.hbm.client.model.LegacyItemTransforms;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;
/** Source ItemRenderBase inventory scale3 and common translation(-0.5,0,0). */
public final class ElectricHeaterItemRenderer extends BlockEntityWithoutLevelRenderer {
 public ElectricHeaterItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
 @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){poses.pushPose();LegacyItemTransforms.held(poses,context);if(context==ItemDisplayContext.GUI){poses.translate(0,1,.5);poses.scale(1F/16,-1F/16,1F/16);poses.translate(8,10,0);poses.mulPose(Axis.XP.rotationDegrees(-30));poses.mulPose(Axis.YP.rotationDegrees(45));poses.scale(-1,-1,-1);poses.scale(3,3,3);}else{if(context==ItemDisplayContext.GROUND){poses.translate(.5,0,.5);poses.scale(1.5F,1.5F,1.5F);}else poses.translate(.5,.25,0);poses.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)poses.mulPose(Axis.YP.rotationDegrees(90));}poses.translate(-.5,0,0);ElectricHeaterRenderer.model(poses,buffers,light,overlay);poses.popPose();}
}
