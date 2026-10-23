package com.hbm.machine.chimney.client;
import com.hbm.client.model.LegacyItemTransforms;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;
public final class ChimneyItemRenderer extends BlockEntityWithoutLevelRenderer {
 private final boolean industrial;public ChimneyItemRenderer(boolean industrial){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());this.industrial=industrial;}
 @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack p,MultiBufferSource buffers,int light,int overlay){p.pushPose();LegacyItemTransforms.held(p,context);if(context==ItemDisplayContext.GUI){p.translate(0,1,.5);p.scale(1F/16,-1F/16,1F/16);p.translate(8,10,0);p.mulPose(Axis.XP.rotationDegrees(-30));p.mulPose(Axis.YP.rotationDegrees(45));p.scale(-1,-1,-1);p.translate(0,-5,0);float scale=industrial?2.75F:2.25F;p.scale(scale,scale,scale);}else{if(context==ItemDisplayContext.GROUND){p.translate(.5,0,.5);p.scale(1.5F,1.5F,1.5F);}else p.translate(.5,.25,0);p.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)p.mulPose(Axis.YP.rotationDegrees(90));}float scale=industrial?.25F:.5F;p.scale(scale,scale,scale);ChimneyRenderer.model(industrial,p,buffers,light,overlay);p.popPose();}
}
