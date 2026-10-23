package com.hbm.client.structure.skeletonholder;
import com.hbm.block.skeletonholder.*;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
/** True source Holder1 only, its4metadata headings, and source renderInFrame held-stack placement. */
public final class SkeletonHolderRenderer implements BlockEntityRenderer<SkeletonHolderEntity> {
 private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/blocks/skeleton_holder.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/particle/skeleton.png");
 public SkeletonHolderRenderer(BlockEntityRendererProvider.Context context){}
 @Override public void render(SkeletonHolderEntity holder,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){poses.pushPose();poses.translate(.5,0,.5);int meta=holder.getBlockState().getValue(SkeletonHolderBlock.META);float yaw=switch(meta){case 2->180;case 4->270;case 5->90;default->0;};poses.mulPose(Axis.YP.rotationDegrees(yaw));var mc=Minecraft.getInstance();var mesh=LegacyObjModels.INSTANCE.get(mc.getResourceManager(),MODEL);LegacyObjRenderer.renderOnly(mesh,poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR_FLAT,"Holder1");
  if(!holder.item.isEmpty()){var stack=holder.item.copy();var model=mc.getItemRenderer().getModel(stack,holder.getLevel(),null,0);boolean block=stack.getItem()instanceof BlockItem&&model.isGui3d();poses.mulPose(Axis.YP.rotationDegrees(90));if(!block)poses.scale(1.5F,1.5F,1.5F);poses.translate(0,.125,0);SkeletonHeldItem.render(stack,model,block,holder,poses,buffers,light,overlay);}poses.popPose();}
}
