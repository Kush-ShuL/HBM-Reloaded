package com.hbm.client.fractiontower;
import com.hbm.client.model.LegacyItemTransforms;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;
/** Source ItemRenderBase tower inventoryY-2.5/scale3.25, spacerY0/scale3.25, both common scale1. */
public final class FractionItemRenderer extends BlockEntityWithoutLevelRenderer {
 private final boolean spacer;public FractionItemRenderer(boolean spacer){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());this.spacer=spacer;}
 @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack p,MultiBufferSource buffers,int light,int overlay){p.pushPose();LegacyItemTransforms.held(p,context);if(context==ItemDisplayContext.GUI){p.translate(0,1,.5);p.scale(1F/16,-1F/16,1F/16);p.translate(8,10,0);p.mulPose(Axis.XP.rotationDegrees(-30));p.mulPose(Axis.YP.rotationDegrees(45));p.scale(-1,-1,-1);if(!spacer)p.translate(0,-2.5,0);p.scale(3.25F,3.25F,3.25F);}else{if(context==ItemDisplayContext.GROUND){p.translate(.5,0,.5);p.scale(1.5F,1.5F,1.5F);}else p.translate(.5,.25,0);p.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)p.mulPose(Axis.YP.rotationDegrees(90));}FractionRenderer.model(spacer,false,p,buffers,light,overlay);p.popPose();}
}
