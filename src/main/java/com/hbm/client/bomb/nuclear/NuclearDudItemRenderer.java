package com.hbm.client.bomb.nuclear;

import com.hbm.bomb.nuclear.NuclearDudItem;
import com.hbm.client.model.LegacyItemTransforms;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** Original ItemRenderBase, 2.125 GUI scale and each dud's separate axial displacement. */
public final class NuclearDudItemRenderer extends BlockEntityWithoutLevelRenderer {
 public NuclearDudItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
 @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){poses.pushPose();LegacyItemTransforms.held(poses,context);if(context==ItemDisplayContext.GUI){poses.translate(0,1,.5);poses.scale(1F/16,-1F/16,1F/16);poses.translate(8,10,0);poses.mulPose(Axis.XP.rotationDegrees(-30));poses.mulPose(Axis.YP.rotationDegrees(45));poses.scale(-1,-1,-1);poses.translate(0,3,0);poses.scale(2.125F,2.125F,2.125F);poses.mulPose(Axis.ZP.rotationDegrees(90));}else{if(context==ItemDisplayContext.GROUND){poses.translate(.5,0,.5);poses.scale(1.5F,1.5F,1.5F);}else poses.translate(.5,.25,0);poses.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)poses.mulPose(Axis.YP.rotationDegrees(90));}poses.mulPose(Axis.YP.rotationDegrees(90));var type=((NuclearDudItem)stack.getItem()).type();switch(type){case CONVENTIONAL->poses.translate(0,0,-.5);case NUKE->poses.translate(0,0,1.25);case SALTED->poses.translate(0,0,.5);default->{}}NuclearDudRenderer.draw(type,poses,buffers,light,overlay);poses.popPose();}
}
