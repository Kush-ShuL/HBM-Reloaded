package com.hbm.client.machine.pa.cooled;
import com.hbm.client.model.LegacyItemTransforms;
import com.hbm.machine.pa.cooled.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;
/** Actual five IItemRendererProvider/ItemRenderBase transforms in every modern physical hand. */
public final class PaItemRenderer extends BlockEntityWithoutLevelRenderer{
 public PaItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
 @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){if(!(stack.getItem() instanceof BlockItem item)||!(item.getBlock() instanceof PaBlock block))return;var kind=block.kind;poses.pushPose();if(LegacyItemTransforms.isHeld(context))LegacyItemTransforms.held(poses,context);else poses.translate(.5,.5,.5);if(context==ItemDisplayContext.GUI){poses.scale(1F/16,-1F/16,1F/16);poses.translate(0,2,0);poses.mulPose(Axis.XP.rotationDegrees(-30));poses.mulPose(Axis.YP.rotationDegrees(45));poses.scale(-1,-1,-1);poses.translate(0,switch(kind){case QUADRUPOLE->-3.5;case DIPOLE->-3;default->-1;},0);float scale=kind==PaKind.DIPOLE?3.5F:kind==PaKind.DETECTOR?3:4;poses.scale(scale,scale,scale);}else{boolean entity=context==ItemDisplayContext.GROUND||context==ItemDisplayContext.FIXED||context==ItemDisplayContext.NONE;if(entity)poses.scale(1.5F,1.5F,1.5F);else poses.translate(.5,.25,0);poses.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)poses.mulPose(Axis.YP.rotationDegrees(90));}if(kind==PaKind.SOURCE||kind==PaKind.RFC||kind==PaKind.DETECTOR)poses.scale(.5F,.5F,.5F);if(kind!=PaKind.DIPOLE)poses.mulPose(Axis.YP.rotationDegrees(90));PaRenderer.draw(kind,poses,buffers,light,overlay);poses.popPose();}
}
