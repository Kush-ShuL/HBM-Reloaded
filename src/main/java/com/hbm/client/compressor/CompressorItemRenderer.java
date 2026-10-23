package com.hbm.client.compressor;
import com.hbm.client.model.LegacyItemTransforms;
import com.hbm.machine.compressor.CompressorContent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;
/** Exact original ItemRenderBase matrices including animated inventory Pump and Fan. */
public final class CompressorItemRenderer extends BlockEntityWithoutLevelRenderer {
 public CompressorItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
 @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){boolean compact=stack.is(CompressorContent.COMPACT_ITEM.get());poses.pushPose();if(LegacyItemTransforms.isHeld(context))LegacyItemTransforms.held(poses,context);else poses.translate(.5,.5,.5);if(context==ItemDisplayContext.GUI){poses.scale(1F/16,-1F/16,1F/16);poses.translate(0,2,0);poses.mulPose(Axis.XP.rotationDegrees(-30));poses.mulPose(Axis.YP.rotationDegrees(45));poses.scale(-1,-1,-1);poses.translate(compact?-1:0,compact?-1:-4,0);float scale=compact?2.75F:3;poses.scale(scale,scale,scale);}else{boolean entity=context==ItemDisplayContext.GROUND||context==ItemDisplayContext.FIXED||context==ItemDisplayContext.NONE;if(entity)poses.scale(1.5F,1.5F,1.5F);else poses.translate(.5,.25,0);poses.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)poses.mulPose(Axis.YP.rotationDegrees(90));}float scale=compact?.75F:.5F;poses.scale(scale,scale,scale);if(compact)poses.translate(.5,0,0);double lift=(System.currentTimeMillis()*.005)%9;if(lift>3)lift=3-(lift-3)/2;CompressorRenderer.render(compact,-lift,(System.currentTimeMillis()*.25)%360,poses,buffers,light,overlay);poses.popPose();}
}
